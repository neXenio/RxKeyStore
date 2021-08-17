package com.nexenio.rxkeystore.provider.cipher;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.BaseCryptoProvider;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import io.reactivex.rxjava3.core.Single;

public abstract class BaseCipherProvider extends BaseCryptoProvider implements RxCipherProvider {

    protected final String keyAlgorithm;
    protected boolean useStrongBoxIfAvailable;

    public BaseCipherProvider(RxKeyStore rxKeyStore, String keyAlgorithm) {
        super(rxKeyStore);
        this.keyAlgorithm = keyAlgorithm;
    }

    @Override
    public Single<Pair<byte[], byte[]>> encrypt(@NonNull byte[] data, @NonNull Key key) {
        return getCipherInstance()
                .flatMap(cipher -> Single.fromCallable(() -> {
                    byte[] encryptedData;
                    byte[] iv;
                    synchronized (cipher) {
                        cipher.init(Cipher.ENCRYPT_MODE, key);
                        encryptedData = cipher.doFinal(data);
                        iv = cipher.getIV();
                    }
                    return new Pair<>(encryptedData, iv);
                })).onErrorResumeNext(throwable -> Single.error(
                        new RxEncryptionException(throwable)
                ));
    }

    @Override
    public Single<byte[]> encrypt(@NonNull byte[] data, @NonNull byte[] initializationVector, @NonNull Key key) {
        return getCipherInstance()
                .flatMap(cipher -> Single.fromCallable(() -> {
                    IvParameterSpec parameterSpec = new IvParameterSpec(initializationVector);
                    byte[] encryptedData;
                    synchronized (cipher) {
                        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
                        encryptedData = cipher.doFinal(data);
                    }
                    return encryptedData;
                })).onErrorResumeNext(throwable -> Single.error(
                        new RxEncryptionException(throwable)
                ));
    }

    @Override
    public Single<byte[]> decrypt(@NonNull byte[] data, @Nullable byte[] initializationVector, @NonNull Key key) {
        return getCipherInstance()
                .flatMap(cipher -> Single.fromCallable(() -> {
                    byte[] decryptedData;
                    synchronized (cipher) {
                        if (initializationVector != null) {
                            IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
                            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
                        } else {
                            cipher.init(Cipher.DECRYPT_MODE, key);
                        }
                        decryptedData = cipher.doFinal(data);
                    }
                    return decryptedData;
                })).onErrorResumeNext(throwable -> Single.error(
                        new RxDecryptionException(throwable)
                ));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected abstract Single<KeyGenParameterSpec> getKeyGenParameterSpec(@NonNull String alias);

    /**
     * Note: {@link Cipher} instances are not thread safe!
     */
    protected Single<Cipher> getCipherInstance() {
        return Single.defer(() -> {
            Cipher cipher;
            boolean useDefaultProvider = rxKeyStore.shouldUseDefaultProvider();
            if (!useDefaultProvider) {
                // due to an issue on older Android SDK versions, we need to use the default provider
                // when requesting a cipher instance instead of the AndroidKeyStore provider.
                // See https://github.com/neXenio/RxKeyStore/issues/23
                boolean isAffectedAndroidVersion = Build.VERSION.SDK_INT <= Build.VERSION_CODES.M;
                boolean isAffectedProvider = rxKeyStore.getProvider().equals(RxKeyStore.PROVIDER_ANDROID_KEY_STORE);
                useDefaultProvider = isAffectedAndroidVersion && isAffectedProvider;
            }
            if (useDefaultProvider) {
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

    protected boolean shouldUseStrongBox() {
        if (!useStrongBoxIfAvailable) {
            return false;
        }
        Boolean supported = rxKeyStore.getIsStrongBoxSupported();
        return supported != null && supported;
    }

    @Override
    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    public boolean getUseStrongBoxIfAvailable() {
        return useStrongBoxIfAvailable;
    }

    public void setUseStrongBoxIfAvailable(boolean useStrongBoxIfAvailable) {
        this.useStrongBoxIfAvailable = useStrongBoxIfAvailable;
    }

}
