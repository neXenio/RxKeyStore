package com.nexenio.rxkeystore.provider.hash;

import com.nexenio.rxkeystore.RxKeyStore;

import androidx.annotation.NonNull;

public class Sha256HashProvider extends BaseHashProvider {

    public Sha256HashProvider(@NonNull RxKeyStore rxKeyStore) {
        super(rxKeyStore, RxHashProvider.SHA_256);
    }

}
