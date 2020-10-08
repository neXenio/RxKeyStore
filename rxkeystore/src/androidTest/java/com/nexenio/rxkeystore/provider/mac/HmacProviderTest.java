package com.nexenio.rxkeystore.provider.mac;

import com.nexenio.rxkeystore.RxKeyStore;

import org.junit.Before;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

public class HmacProviderTest extends BaseMacProviderTest {

    @CallSuper
    @Before
    @Override
    public void setUpBeforeEachTest() {
        super.setUpBeforeEachTest();
    }

    @Override
    protected RxMacProvider createMacProvider(@NonNull RxKeyStore keyStore) {
        return new HmacProvider(keyStore, HmacProvider.HASH_ALGORITHM_SHA256);
    }

}