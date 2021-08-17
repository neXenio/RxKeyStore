package com.nexenio.rxkeystore.provider.hash;

import androidx.annotation.NonNull;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.BaseCryptoProvider;
import com.nexenio.rxkeystore.provider.mac.RxMacException;

import java.security.MessageDigest;

import io.reactivex.rxjava3.core.Single;

public class BaseHashProvider extends BaseCryptoProvider implements RxHashProvider {

    protected String hashAlgorithm;

    public BaseHashProvider(@NonNull RxKeyStore rxKeyStore, @NonNull String hashAlgorithm) {
        super(rxKeyStore);
        this.hashAlgorithm = hashAlgorithm;
    }

    @Override
    public Single<byte[]> hash(@NonNull byte[] data) {
        return getMessageDigestInstance()
                .map(messageDigest -> {
                    byte[] digest;
                    synchronized (messageDigest) {
                        messageDigest.update(data);
                        digest = messageDigest.digest();
                    }
                    return digest;
                })
                .onErrorResumeNext(throwable -> Single.error(
                        new RxHashException("Unable generate hash", throwable)
                ));
    }

    /**
     * Note: {@link MessageDigest} instances are not thread safe!
     */
    protected Single<MessageDigest> getMessageDigestInstance() {
        return Single.defer(() -> {
            MessageDigest messageDigest;
            if (rxKeyStore.shouldUseDefaultProvider()) {
                messageDigest = MessageDigest.getInstance(hashAlgorithm);
            } else {
                messageDigest = MessageDigest.getInstance(hashAlgorithm, rxKeyStore.getProvider());
            }
            return Single.just(messageDigest);
        }).onErrorResumeNext(throwable -> Single.error(
                new RxMacException("Unable to get MessageDigest instance: " + hashAlgorithm, throwable)
        ));
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

}
