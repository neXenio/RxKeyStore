package com.nexenio.rxkeystore.provider.signature;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.BaseCryptoProvider;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import androidx.annotation.NonNull;
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
                .map(signature -> {
                    signature.initSign(privateKey);
                    signature.update(data);
                    return signature.sign();
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
                    signatureInstance.initVerify(publicKey);
                    signatureInstance.update(data);
                    return signatureInstance.verify(signature);
                }).onErrorReturnItem(false);
    }

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
