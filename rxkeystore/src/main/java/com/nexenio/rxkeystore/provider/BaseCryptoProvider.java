package com.nexenio.rxkeystore.provider;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Pair;

import com.nexenio.rxkeystore.RxKeyStore;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import io.reactivex.Single;

public abstract class BaseCryptoProvider implements RxCryptoProvider {

    protected final RxKeyStore rxKeyStore;

    protected final String keyAlgorithm;

    public BaseCryptoProvider(RxKeyStore rxKeyStore, String keyAlgorithm) {
        this.rxKeyStore = rxKeyStore;
        this.keyAlgorithm = keyAlgorithm;
    }

    @Override
    public Single<Pair<byte[], byte[]>> encrypt(@NonNull byte[] data, @NonNull Key key) {
        return getCipherInstance()
                .flatMap(cipher -> Single.fromCallable(() -> {
                    cipher.init(Cipher.ENCRYPT_MODE, key);
                    byte[] encryptedData = cipher.doFinal(data);
                    return new Pair<>(encryptedData, cipher.getIV());
                }));
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
                }));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected abstract Single<KeyGenParameterSpec> getKeyGenParameterSpec(@NonNull String alias);

    protected Single<Cipher> getCipherInstance() {
        return Single.defer(
                () -> Single.just(Cipher.getInstance(getTransformationAlgorithm()))
        );
    }

    protected abstract String getTransformationAlgorithm();

    @Override
    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

}
