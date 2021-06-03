package com.nexenio.rxkeystore.provider.cipher.symmetric.aes;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.cipher.symmetric.BaseSymmetricCryptoProviderTest;
import com.nexenio.rxkeystore.provider.cipher.symmetric.RxSymmetricCipherProvider;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

public class RxAESCryptoProviderTest extends BaseSymmetricCryptoProviderTest {

    @BeforeClass
    public static void setUpBeforeClass() {
        setupSecurityProviders();
    }

    @AfterClass
    public static void cleanUpAfterClass() {
        cleanUpSecurityProviders();
    }

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