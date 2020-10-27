package com.nexenio.rxkeystore.provider.mac;

import com.nexenio.rxkeystore.provider.RxCryptoProvider;

import javax.crypto.SecretKey;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface RxMacProvider extends RxCryptoProvider {

    Single<byte[]> sign(@NonNull byte[] data, @NonNull SecretKey secretKey);

    Completable verify(@NonNull byte[] data, @NonNull byte[] signature, @NonNull SecretKey secretKey);

    Single<Boolean> getVerificationResult(@NonNull byte[] data, @NonNull byte[] signature, @NonNull SecretKey secretKey);

}
