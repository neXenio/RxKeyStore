package com.nexenio.rxkeystore.provider.symmetric;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.BaseCryptoProvider;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import io.reactivex.Maybe;
import io.reactivex.Single;

@RequiresApi(api = Build.VERSION_CODES.M)
public abstract class BaseSymmetricCryptoProvider extends BaseCryptoProvider implements RxSymmetricCryptoProvider {

    public BaseSymmetricCryptoProvider(RxKeyStore rxKeyStore, String keyAlgorithm) {
        super(rxKeyStore, keyAlgorithm);
    }

    @Override
    public Single<SecretKey> generateKey(@NonNull String alias, @NonNull Context context) {
        return getKeyAlgorithmParameterSpec(alias, context)
                .map(algorithmParameterSpec -> {
                    KeyGenerator keyGenerator = KeyGenerator.getInstance(keyAlgorithm, rxKeyStore.getKeyStoreType());
                    keyGenerator.init(algorithmParameterSpec);
                    return keyGenerator.generateKey();
                });
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

    @Override
    public Single<SecretKey> getKey(@NonNull String alias) {
        return rxKeyStore.getKey(alias).cast(SecretKey.class);
    }

    @Override
    public Maybe<SecretKey> getKeyIfAvailable(@NonNull String alias) {
        return rxKeyStore.getKeyIfAvailable(alias).cast(SecretKey.class);
    }

}