package com.nexenio.rxkeystore.provider.hash;

import com.nexenio.rxkeystore.RxKeyStore;

import org.junit.Before;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

public class Md5HashProviderTest extends BaseHashProviderTest {

    @CallSuper
    @Before
    @Override
    public void setUpBeforeEachTest() {
        super.setUpBeforeEachTest();
    }

    @Override
    protected RxHashProvider createHashProvider(@NonNull RxKeyStore keyStore) {
        return new Md5HashProvider(keyStore);
    }

}