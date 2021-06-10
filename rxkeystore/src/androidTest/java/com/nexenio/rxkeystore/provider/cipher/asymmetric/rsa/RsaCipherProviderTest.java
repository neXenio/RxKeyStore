package com.nexenio.rxkeystore.provider.cipher.asymmetric.rsa;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.cipher.RxEncryptionException;
import com.nexenio.rxkeystore.provider.cipher.asymmetric.BaseAsymmetricCipherProviderTest;
import com.nexenio.rxkeystore.provider.cipher.asymmetric.RxAsymmetricCipherProvider;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

public class RsaCipherProviderTest extends BaseAsymmetricCipherProviderTest {

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
    protected RxAsymmetricCipherProvider createAsymmetricCryptoProvider(@NonNull RxKeyStore keyStore) {
        return new RsaCipherProvider(keyStore);
    }

    @Ignore("DHKeyAgreement requires DHPrivateKey")
    @Test
    @Override
    public void generateSecretKey_matchingKeyPairs_sameSecretKey() {
        super.generateSecretKey_matchingKeyPairs_sameSecretKey();
    }

    @Test
    public void encrypt_invalidDataLength_emitsError() {
        byte[] unencryptedBytes = LOREM_IPSUM_LONG.getBytes(StandardCharsets.UTF_8);

        asymmetricCryptoProvider.generateKeyPair(ALIAS_NEW, context)
                .flatMap(keyPair -> asymmetricCryptoProvider.encrypt(unencryptedBytes, keyPair.getPublic())
                        .flatMap(encryptedBytesAndIV -> asymmetricCryptoProvider.decrypt(encryptedBytesAndIV.first, encryptedBytesAndIV.second, keyPair.getPrivate())))
                .test()
                .assertError(RxEncryptionException.class);
    }

}