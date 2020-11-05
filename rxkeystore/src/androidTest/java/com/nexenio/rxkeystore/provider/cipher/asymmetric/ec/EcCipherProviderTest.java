package com.nexenio.rxkeystore.provider.cipher.asymmetric.ec;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.cipher.asymmetric.BaseAsymmetricCipherProviderTest;
import com.nexenio.rxkeystore.provider.cipher.asymmetric.RxAsymmetricCipherProvider;

import org.junit.Before;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

public class EcCipherProviderTest extends BaseAsymmetricCipherProviderTest {

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