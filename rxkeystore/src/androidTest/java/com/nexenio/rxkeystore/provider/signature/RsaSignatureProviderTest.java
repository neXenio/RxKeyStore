package com.nexenio.rxkeystore.provider.signature;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.cipher.asymmetric.RxAsymmetricCipherProvider;
import com.nexenio.rxkeystore.provider.cipher.asymmetric.rsa.RsaCipherProvider;

import org.junit.Before;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

public class RsaSignatureProviderTest extends BaseSignatureProviderTest {

    @CallSuper
    @Before
    @Override
    public void setUpBeforeEachTest() {
        super.setUpBeforeEachTest();
    }

    @Override
    protected RxSignatureProvider createSignatureProvider(@NonNull RxKeyStore keyStore) {
        return new BaseSignatureProvider(keyStore, "SHA256withRSA");
    }

    @Override
    protected RxAsymmetricCipherProvider createAsymmetricCipherProvider(@NonNull RxKeyStore keyStore) {
        return new RsaCipherProvider(keyStore);
    }

}
