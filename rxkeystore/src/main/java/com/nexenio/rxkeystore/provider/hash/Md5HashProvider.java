package com.nexenio.rxkeystore.provider.hash;

import com.nexenio.rxkeystore.RxKeyStore;

import androidx.annotation.NonNull;

public class Md5HashProvider extends BaseHashProvider {

    public Md5HashProvider(@NonNull RxKeyStore rxKeyStore) {
        super(rxKeyStore, RxHashProvider.MD5);
    }

}
