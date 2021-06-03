package com.nexenio.rxkeystore.provider.cipher.asymmetric.ec;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.cipher.asymmetric.BaseAsymmetricCipherProviderTest;
import com.nexenio.rxkeystore.provider.cipher.asymmetric.RxAsymmetricCipherProvider;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

public class EcCipherProviderTest extends BaseAsymmetricCipherProviderTest {

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
    protected RxAsymmetricCipherProvider createAsymmetricCryptoProvider(@NonNull RxKeyStore keyStore) {
        return new EcCipherProvider(keyStore);
    }

}