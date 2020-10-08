package com.nexenio.rxkeystore.provider.cipher;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Pair;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.BaseCryptoProvider;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import io.reactivex.rxjava3.core.Single;

public abstract class BaseCipherProvider extends BaseCryptoProvider implements RxCipherProvider {

    protected final String keyAlgorithm;

    public BaseCipherProvider(RxKeyStore rxKeyStore, String keyAlgorithm) {
        super(rxKeyStore);
        this.keyAlgorithm = keyAlgorithm;
    }

    @Override
    public Single<Pair<byte[], byte[]>> encrypt(@NonNull byte[] data, @NonNull Key key) {
        return getCipherInstance()
                .flatMap(cipher -> Single.fromCallable(() -> {
                    cipher.init(Cipher.ENCRYPT_MODE, key);
                    byte[] encryptedData = cipher.doFinal(data);
                    return new Pair<>(encryptedData, cipher.getIV());
                })).onErrorResumeNext(throwable -> Single.error(
                        new RxEncryptionException(throwable)
                ));
    }

    @Override
    public Single<byte[]> decrypt(@NonNull byte[] data, @Nullable byte[] initializationVector, @NonNull Key key) {
        return getCipherInstance()
                .flatMap(cipher -> Single.fromCallable(() -> {
                    if (initializationVector != null) {
                        IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
                        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
                    } else {
                        cipher.init(Cipher.DECRYPT_MODE, key);
                    }
                    return cipher.doFinal(data);
                })).onErrorResumeNext(throwable -> Single.error(
                        new RxDecryptionException(throwable)
                ));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected abstract Single<KeyGenParameterSpec> getKeyGenParameterSpec(@NonNull String alias);

    protected Single<Cipher> getCipherInstance() {
        return Single.defer(() -> {
            Cipher cipher;
            if (rxKeyStore.shouldUseDefaultProvider()) {
                cipher = Cipher.getInstance(getTransformationAlgorithm());
            } else {
                cipher = Cipher.getInstance(getTransformationAlgorithm(), rxKeyStore.getProvider());
            }
            return Single.just(cipher);
        }).onErrorResumeNext(throwable -> Single.error(
                new RxCipherProviderException("Unable to get Cipher instance: " + getTransformationAlgorithm(), throwable)
        ));
    }

    protected abstract String getTransformationAlgorithm();

    @Override
    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

}
