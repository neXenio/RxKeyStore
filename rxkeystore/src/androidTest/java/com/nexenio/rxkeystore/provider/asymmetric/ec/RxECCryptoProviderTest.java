package com.nexenio.rxkeystore.provider.asymmetric.ec;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.asymmetric.BaseAsymmetricCryptoProviderTest;
import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;

import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;
import java.util.Objects;

import androidx.annotation.NonNull;
import io.reactivex.Single;

public class RxECCryptoProviderTest extends BaseAsymmetricCryptoProviderTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Override
    protected RxAsymmetricCryptoProvider createAsymmetricCryptoProvider(@NonNull RxKeyStore keyStore) {
        return new RxECCryptoProvider(keyStore);
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