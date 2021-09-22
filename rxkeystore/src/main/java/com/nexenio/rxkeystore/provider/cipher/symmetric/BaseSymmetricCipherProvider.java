package com.nexenio.rxkeystore.provider.cipher.symmetric;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.cipher.BaseCipherProvider;

import java.security.KeyStore;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@RequiresApi(api = Build.VERSION_CODES.M)
public abstract class BaseSymmetricCipherProvider extends BaseCipherProvider implements RxSymmetricCipherProvider {

    public BaseSymmetricCipherProvider(RxKeyStore rxKeyStore, String keyAlgorithm) {
        super(rxKeyStore, keyAlgorithm);
    }

    @Override
    public Single<SecretKey> generateKey(@NonNull String alias, @NonNull Context context) {
        return generateKey(alias, null, context);
    }

    @Override
    public Single<SecretKey> generateKey(@NonNull String alias, @Nullable Integer keyPurposes, @NonNull Context context) {
        return Single.defer(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int purposes;
                if (keyPurposes == null) {
                    purposes = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY;
                } else {
                    purposes = keyPurposes;
                }
                return getKeyGeneratorInstance()
                        .flatMap(keyGenerator -> getKeyAlgorithmParameterSpec(alias, purposes, context)
                                .map(algorithmParameterSpec -> {
                                    SecretKey secretKey;
                                    synchronized (keyGenerator) {
                                        keyGenerator.init(algorithmParameterSpec);
                                        secretKey = keyGenerator.generateKey();
                                    }
                                    return secretKey;
                                }));
            } else {
                return Single.error(new IllegalStateException("Symmetric keys are not supported by the keystore on Android versions before M"));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public Single<AlgorithmParameterSpec> getKeyAlgorithmParameterSpec(@NonNull String alias, @NonNull Context context) {
        return rxKeyStore.checkIfStrongBoxIsSupported(context)
                .andThen(Single.defer(() -> getKeyGenParameterSpec(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public Single<AlgorithmParameterSpec> getKeyAlgorithmParameterSpec(@NonNull String alias, int keyPurposes, @NonNull Context context) {
        return rxKeyStore.checkIfStrongBoxIsSupported(context)
                .andThen(Single.defer(() -> getKeyGenParameterSpec(alias, keyPurposes)));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected Single<KeyGenParameterSpec> getKeyGenParameterSpec(@NonNull String alias, int keyPurposes) {
        return Single.fromCallable(() -> {
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(alias, keyPurposes)
                    .setBlockModes(getBlockModes())
                    .setEncryptionPaddings(getEncryptionPaddings());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                builder.setIsStrongBoxBacked(shouldUseStrongBox());
            }

            return builder.build();
        });
    }

    protected abstract String[] getBlockModes();

    protected abstract String[] getEncryptionPaddings();

    /**
     * Note: {@link KeyGenerator} instances are not thread safe!
     */
    protected Single<KeyGenerator> getKeyGeneratorInstance() {
        return Single.defer(() -> {
            KeyGenerator keyGenerator;
            if (rxKeyStore.shouldUseDefaultProvider()) {
                keyGenerator = KeyGenerator.getInstance(getKeyAlgorithm());
            } else {
                keyGenerator = KeyGenerator.getInstance(getKeyAlgorithm(), rxKeyStore.getProvider());
            }
            return Single.just(keyGenerator);
        });
    }

    @Override
    public Single<SecretKey> getKey(@NonNull String alias) {
        return rxKeyStore.getKey(alias).cast(SecretKey.class);
    }

    @Override
    public Maybe<SecretKey> getKeyIfAvailable(@NonNull String alias) {
        return rxKeyStore.getKeyIfAvailable(alias).cast(SecretKey.class);
    }

    @Override
    public Completable setKey(@NonNull String alias, @NonNull SecretKey secretKey) {
        return Completable.defer(() -> {
            KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(secretKey);
            return setKey(alias, entry);
        });
    }

    @Override
    public Completable setKey(@NonNull String alias, @NonNull KeyStore.SecretKeyEntry secretKeyEntry) {
        return rxKeyStore.setEntry(alias, secretKeyEntry);
    }

}
