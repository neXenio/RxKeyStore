package com.nexenio.rxkeystore.provider.signature;

import com.nexenio.rxkeystore.provider.RxCryptoProvider;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface RxSignatureProvider extends RxCryptoProvider {

    Single<byte[]> sign(@NonNull byte[] data, @NonNull PrivateKey privateKey);

    Completable verify(@NonNull byte[] data, @NonNull byte[] signature, @NonNull PublicKey publicKey);

    Single<Boolean> getVerificationResult(@NonNull byte[] data, @NonNull byte[] signature, @NonNull PublicKey publicKey);

}
