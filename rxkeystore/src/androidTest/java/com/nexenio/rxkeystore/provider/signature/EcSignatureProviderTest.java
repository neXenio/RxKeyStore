package com.nexenio.rxkeystore.provider.signature;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.cipher.asymmetric.RxAsymmetricCipherProvider;
import com.nexenio.rxkeystore.provider.cipher.asymmetric.ec.EcCipherProvider;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

public class EcSignatureProviderTest extends BaseSignatureProviderTest {

    @BeforeClass
    public static void setUpBeforeClass() {
        setupSecurityProviders();
    }

    @AfterClass
    public static void cleanUpAfterClass() {
        cleanUpSecurityProviders();
    }

    @CallSuper
    @Before
    @Override
    public void setUpBeforeEachTest() {
        super.setUpBeforeEachTest();
    }

    @Override
    protected RxSignatureProvider createSignatureProvider(@NonNull RxKeyStore keyStore) {
        return new BaseSignatureProvider(keyStore, "SHA256withECDSA");
    }

    @Override
    protected RxAsymmetricCipherProvider createAsymmetricCipherProvider(@NonNull RxKeyStore keyStore) {
        return new EcCipherProvider(keyStore);
    }

    @Override
    @Ignore("Unclear if this is the expected behaviour")
    @Test
    public void sign_subsequentCallsWithSameKey_emitsSameSignature() {
        super.sign_subsequentCallsWithSameKey_emitsSameSignature();
    }

}
