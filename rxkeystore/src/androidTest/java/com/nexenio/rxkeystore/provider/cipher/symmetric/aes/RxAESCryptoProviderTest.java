package com.nexenio.rxkeystore.provider.cipher.symmetric.aes;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.cipher.symmetric.BaseSymmetricCryptoProviderTest;
import com.nexenio.rxkeystore.provider.cipher.symmetric.RxSymmetricCipherProvider;

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
    protected RxSymmetricCipherProvider createSymmetricCryptoProvider(@NonNull RxKeyStore keyStore) {
        return new AesCipherProvider(keyStore);
    }

}