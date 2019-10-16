package com.nexenio.rxkeystore.provider.asymmetric.ec;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.asymmetric.BaseAsymmetricCryptoProviderTest;
import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import androidx.annotation.NonNull;

public class RxECCryptoProviderTest extends BaseAsymmetricCryptoProviderTest {

    @Before
    @Override
    public void setUpBeforeEachTest() {
        super.setUpBeforeEachTest();
    }

    @Override
    protected RxAsymmetricCryptoProvider createAsymmetricCryptoProvider(@NonNull RxKeyStore keyStore) {
        return new RxECCryptoProvider(keyStore);
    }

    @Ignore("Unclear if this is the expected behaviour")
    @Test
    @Override
    public void sign_subsequentCallsWithSameKey_emitsSameSignature() {
        super.sign_subsequentCallsWithSameKey_emitsSameSignature();
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