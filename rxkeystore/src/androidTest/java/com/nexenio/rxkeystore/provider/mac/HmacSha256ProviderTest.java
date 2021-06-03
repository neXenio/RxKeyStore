package com.nexenio.rxkeystore.provider.mac;

import com.nexenio.rxkeystore.RxKeyStore;

import org.junit.Before;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

public class HmacSha256ProviderTest extends BaseMacProviderTest {

    @CallSuper
    @Before
    @Override
    public void setUpBeforeEachTest() {
        super.setUpBeforeEachTest();
    }

    @Override
    protected RxMacProvider createMacProvider(@NonNull RxKeyStore keyStore) {
        return new HmacSha256Provider(keyStore);
    }

}