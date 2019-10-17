package com.nexenio.rxkeystore.provider.symmetric.aes;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.symmetric.BaseSymmetricCryptoProviderTest;
import com.nexenio.rxkeystore.provider.symmetric.RxSymmetricCryptoProvider;

import org.junit.Before;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

public class RxAESCryptoProviderTest extends BaseSymmetricCryptoProviderTest {

    @CallSuper
    @Before
    @Override
    public void setUpBeforeEachTest() {
        super.setUpBeforeEachTest();
    }

    @Override
    protected RxSymmetricCryptoProvider createSymmetricCryptoProvider(@NonNull RxKeyStore keyStore) {
        return new RxAESCryptoProvider(keyStore);
    }

}