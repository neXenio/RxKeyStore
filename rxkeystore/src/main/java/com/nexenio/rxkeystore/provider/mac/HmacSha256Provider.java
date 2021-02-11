package com.nexenio.rxkeystore.provider.mac;

import com.nexenio.rxkeystore.RxKeyStore;

import androidx.annotation.NonNull;

public class HmacSha256Provider extends BaseMacProvider {

    public HmacSha256Provider(@NonNull RxKeyStore rxKeyStore) {
        super(rxKeyStore, RxMacProvider.HMAC_SHA256);
    }

}
