package com.nexenio.rxkeystore.provider;

import com.nexenio.rxkeystore.RxKeyStore;

public abstract class BaseCryptoProvider implements RxCryptoProvider {

    protected final RxKeyStore rxKeyStore;

    public BaseCryptoProvider(RxKeyStore rxKeyStore) {
        this.rxKeyStore = rxKeyStore;
    }

}
