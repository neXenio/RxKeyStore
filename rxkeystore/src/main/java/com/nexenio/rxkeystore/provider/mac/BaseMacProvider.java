package com.nexenio.rxkeystore.provider.mac;

import androidx.annotation.NonNull;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.BaseCryptoProvider;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class BaseMacProvider extends BaseCryptoProvider implements RxMacProvider {

    protected String macAlgorithm;

    public BaseMacProvider(@NonNull RxKeyStore rxKeyStore, @NonNull String macAlgorithm) {
        super(rxKeyStore);
        this.macAlgorithm = macAlgorithm;
    }

    @Override
    public Single<byte[]> sign(@NonNull byte[] data, @NonNull SecretKey secretKey) {
        return getMacInstance()
                .map(macInstance -> {
                    byte[] mac;
                    synchronized (macInstance) {
                        macInstance.init(secretKey);
                        mac = macInstance.doFinal(data);
                    }
                    return mac;
                })
                .onErrorResumeNext(throwable -> Single.error(
                        new RxMacException("Unable generate message authentication code", throwable)
                ));
    }

    @Override
    public Completable verify(@NonNull byte[] data, @NonNull byte[] signature, @NonNull SecretKey secretKey) {
        return getVerificationResult(data, signature, secretKey)
                .flatMapCompletable(verificationResult -> Completable.defer(() -> {
                    if (verificationResult) {
                        return Completable.complete();
                    } else {
                        return Completable.error(new RxMacException("Message authentication code is not valid"));
                    }
                }));
    }

    @Override
    public Single<Boolean> getVerificationResult(@NonNull byte[] data, @NonNull byte[] signature, @NonNull SecretKey secretKey) {
        return sign(data, secretKey)
                .map(computedSignature -> isEqual(computedSignature, signature))
                .onErrorReturnItem(false);
    }

    /**
     * Note: {@link Mac} instances are not thread safe!
     */
    protected Single<Mac> getMacInstance() {
        return Single.defer(() -> {
            Mac mac;
            if (rxKeyStore.shouldUseDefaultProvider()) {
                mac = Mac.getInstance(macAlgorithm);
            } else {
                mac = Mac.getInstance(macAlgorithm, rxKeyStore.getProvider());
            }
            return Single.just(mac);
        }).onErrorResumeNext(throwable -> Single.error(
                new RxMacException("Unable to get Mac instance: " + macAlgorithm, throwable)
        ));
    }

    public static boolean isEqual(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        byte result = 0;
        for (byte i = 0; i < a.length; i++) {
            result |= (byte) (a[i] ^ b[i]);
        }
        return result == 0;
    }

    public String getMacAlgorithm() {
        return macAlgorithm;
    }

    public void setMacAlgorithm(String macAlgorithm) {
        this.macAlgorithm = macAlgorithm;
    }

}
