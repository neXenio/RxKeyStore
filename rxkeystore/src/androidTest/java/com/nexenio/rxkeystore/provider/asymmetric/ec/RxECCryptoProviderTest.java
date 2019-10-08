package com.nexenio.rxkeystore.provider.asymmetric.ec;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.asymmetric.BaseAsymmetricCryptoProviderTest;
import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.security.Provider;
import java.security.Security;

import androidx.annotation.NonNull;

public class RxECCryptoProviderTest extends BaseAsymmetricCryptoProviderTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Override
    protected RxKeyStore createKeyStore() {
        return super.createKeyStore();
        //return new RxKeyStore(RxKeyStore.TYPE_BKS, RxKeyStore.PROVIDER_BOUNCY_CASTLE);
    }

    @Override
    protected RxAsymmetricCryptoProvider createAsymmetricCryptoProvider(@NonNull RxKeyStore keyStore) {
        return new RxECCryptoProvider(keyStore);
    }

    @Override
    @Ignore("The default provider doesn't support AndroidKeyStoreECPrivateKey." +
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

    @Test
    public void getSignatureAlgorithm() {
    }

}