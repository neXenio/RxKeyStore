package com.nexenio.rxkeystore.provider.symmetric.aes;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.symmetric.BaseSymmetricCryptoProviderTest;
import com.nexenio.rxkeystore.provider.symmetric.RxSymmetricCryptoProvider;

import org.junit.Before;
import org.junit.Test;

import androidx.annotation.NonNull;

public class RxAESCryptoProviderTest extends BaseSymmetricCryptoProviderTest {

    @Before
    @Override
    public void setUpBeforeEachTest() {
        super.setUpBeforeEachTest();
    }

    @Override
    protected RxSymmetricCryptoProvider createSymmetricCryptoProvider(@NonNull RxKeyStore keyStore) {
        return new RxAESCryptoProvider(keyStore);
    }

    @Test
    public void getBlockModes() {
    }

    @Test
    public void getEncryptionPaddings() {
    }

    @Test
    public void getTransformationAlgorithm() {
    }

}