package com.nexenio.rxkeystore.provider.asymmetric.rsa;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.asymmetric.BaseAsymmetricCryptoProviderTest;
import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;

import org.junit.Before;
import org.junit.Ignore;
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

    @Override
    @Ignore("The default provider doesn't support AndroidKeyStoreRSAPrivateKey." +
            "For that to work, a custom provider (e.g. Bouncy Castle) needs to be used.")
    public void generateSecretKey_matchingKeyPairs_sameSecretKey() {
        //super.generateSecretKey_matchingKeyPairs_sameSecretKey();
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