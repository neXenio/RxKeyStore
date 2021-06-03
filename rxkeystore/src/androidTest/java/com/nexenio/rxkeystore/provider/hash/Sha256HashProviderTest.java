package com.nexenio.rxkeystore.provider.hash;

import com.nexenio.rxkeystore.RxKeyStore;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

public class Sha256HashProviderTest extends BaseHashProviderTest {

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
    protected RxHashProvider createHashProvider(@NonNull RxKeyStore keyStore) {
        return new Sha256HashProvider(keyStore);
    }

}