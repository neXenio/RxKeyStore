package com.nexenio.rxkeystore.provider.signature;

import androidx.annotation.NonNull;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.BaseCryptoProvider;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class BaseSignatureProvider extends BaseCryptoProvider implements RxSignatureProvider {

    protected String signatureAlgorithm;

    public BaseSignatureProvider(@NonNull RxKeyStore rxKeyStore, @NonNull String signatureAlgorithm) {
        super(rxKeyStore);
        this.signatureAlgorithm = signatureAlgorithm;
    }

    @Override
    public Single<byte[]> sign(@NonNull byte[] data, @NonNull PrivateKey privateKey) {
        return getSignatureInstance()
                .map(signatureInstance -> {
                    byte[] signature;
                    synchronized (signatureInstance) {
                        signatureInstance.initSign(privateKey);
                        signatureInstance.update(data);
                        signature = signatureInstance.sign();
                    }
                    return signature;
                }).onErrorResumeNext(throwable -> Single.error(
                        new RxSignatureException("Unable to create signature", throwable)
                ));
    }

    @Override
    public Completable verify(@NonNull byte[] data, @NonNull byte[] signature, @NonNull PublicKey publicKey) {
        return getVerificationResult(data, signature, publicKey)
                .flatMapCompletable(verificationResult -> Completable.defer(() -> {
                    if (verificationResult) {
                        return Completable.complete();
                    } else {
                        return Completable.error(new RxSignatureException("Signature is not valid"));
                    }
                }));
    }

    @Override
    public Single<Boolean> getVerificationResult(@NonNull byte[] data, @NonNull byte[] signature, @NonNull PublicKey publicKey) {
        return getSignatureInstance()
                .map(signatureInstance -> {
                    boolean verificationResult;
                    synchronized (signatureInstance) {
                        signatureInstance.initVerify(publicKey);
                        signatureInstance.update(data);
                        verificationResult = signatureInstance.verify(signature);
                    }
                    return verificationResult;
                }).onErrorReturnItem(false);
    }

    /**
     * Note: {@link Signature} instances are not thread safe!
     */
    protected Single<Signature> getSignatureInstance() {
        return Single.defer(() -> {
            Signature signature;
            if (rxKeyStore.shouldUseDefaultProvider()) {
                signature = Signature.getInstance(signatureAlgorithm);
            } else {
                signature = Signature.getInstance(signatureAlgorithm, rxKeyStore.getProvider());
            }
            return Single.just(signature);
        }).onErrorResumeNext(throwable -> Single.error(
                new RxSignatureException("Unable to get Signature instance: " + signatureAlgorithm, throwable)
        ));
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

}
