package com.nexenio.rxkeystore.provider.asymmetric.rsa;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.asymmetric.BaseAsymmetricCryptoProviderTest;
import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;

import org.junit.Before;
import org.junit.Test;

import androidx.annotation.NonNull;

public class RxRSACryptoProviderTest extends BaseAsymmetricCryptoProviderTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Override
    protected RxAsymmetricCryptoProvider createAsymmetricCryptoProvider(@NonNull RxKeyStore keyStore) {
        return new RxRSACryptoProvider(keyStore);
    }

    @Test
    public void getBlockModes() {
    }

    @Test
    public void getEncryptionPaddings() {
    }

    @Test
    public void getSignaturePaddings() {
    }

    @Test
    public void getDigests() {
    }

    @Test
    public void getTransformationAlgorithm() {
    }

}