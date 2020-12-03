package com.nexenio.rxkeystore.provider.hash;

import com.nexenio.rxkeystore.provider.RxCryptoProvider;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Single;

public interface RxHashProvider extends RxCryptoProvider {

    String MD5 = "MD5";
    String SHA_1 = "SHA-1";
    String SHA_256 = "SHA-256";
    String SHA_512 = "SHA-512";

    Single<byte[]> hash(@NonNull byte[] data);

}
