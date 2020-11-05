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
        return getKeyGeneratorInstance()
                .flatMap(keyGenerator -> getKeyAlgorithmParameterSpec(alias, context)
                        .map(algorithmParameterSpec -> {
                            keyGenerator.init(algorithmParameterSpec);
                            return keyGenerator.generateKey();
                        }));
    }

    @Override
    public Single<AlgorithmParameterSpec> getKeyAlgorithmParameterSpec(@NonNull String alias, @NonNull Context context) {
        return Single.defer(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return getKeyGenParameterSpec(alias);
            } else {
                return Single.error(new IllegalStateException("Symmetric keys are not supported by the keystore on Android versions before M"));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected Single<KeyGenParameterSpec> getKeyGenParameterSpec(@NonNull String alias) {
        return Single.fromCallable(() -> {
            int keyPurposes = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY;
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(alias, keyPurposes)
                    .setBlockModes(getBlockModes())
                    .setEncryptionPaddings(getEncryptionPaddings());
            return builder.build();
        });
    }

    protected abstract String[] getBlockModes();

    protected abstract String[] getEncryptionPaddings();

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
