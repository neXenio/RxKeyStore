package com.nexenio.rxkeystore.provider.mac;

import com.nexenio.rxkeystore.RxKeyStore;

import androidx.annotation.NonNull;

@Deprecated
public class HmacProvider extends BaseMacProvider {

    public static final String HASH_ALGORITHM_MD5 = "MD5";
    public static final String HASH_ALGORITHM_SHA1 = "SHA1";
    public static final String HASH_ALGORITHM_SHA256 = "SHA256";
    public static final String HASH_ALGORITHM_SHA512 = "SHA512";

    private static final String MAC_ALGORITHM_PREFIX = "Hmac";

    public HmacProvider(@NonNull RxKeyStore rxKeyStore, @NonNull String hashAlgorithm) {
        super(rxKeyStore, MAC_ALGORITHM_PREFIX + hashAlgorithm);
    }

}
