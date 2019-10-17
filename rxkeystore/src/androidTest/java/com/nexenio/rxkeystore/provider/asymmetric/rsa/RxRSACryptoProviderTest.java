package com.nexenio.rxkeystore.provider.asymmetric.rsa;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.asymmetric.BaseAsymmetricCryptoProviderTest;
import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

public class RxRSACryptoProviderTest extends BaseAsymmetricCryptoProviderTest {

    @CallSuper
    @Before
    @Override
    public void setUpBeforeEachTest() {
        super.setUpBeforeEachTest();
    }

    @Override
    protected RxAsymmetricCryptoProvider createAsymmetricCryptoProvider(@NonNull RxKeyStore keyStore) {
        return new RxRSACryptoProvider(keyStore);
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
                .assertError(ArrayIndexOutOfBoundsException.class);
    }

}