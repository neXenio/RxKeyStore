package com.nexenio.rxkeystore.provider.cipher.asymmetric.rsa;

import android.security.keystore.KeyProperties;
import android.util.Base64;

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

    protected RxAsymmetricCipherProvider defaultProvider;
    protected RxAsymmetricCipherProvider bouncyCastleProvider;

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
        bouncyCastleProvider = asymmetricCryptoProvider;
        defaultProvider = createAsymmetricCryptoProvider(new RxKeyStore());
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

        bouncyCastleProvider.generateKeyPair(ALIAS_NEW, context)
                .flatMap(keyPair -> bouncyCastleProvider.encrypt(unencryptedBytes, keyPair.getPublic())
                        .flatMap(encryptedBytesAndIV -> bouncyCastleProvider.decrypt(encryptedBytesAndIV.first, encryptedBytesAndIV.second, keyPair.getPrivate())))
                .test()
                .assertError(RxEncryptionException.class);
    }

    @Test
    public void encrypt_publicKey$wrongPurpose$defaultProvider_emitsNoError() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        defaultProvider.generateKeyPair(ALIAS_NEW, KeyProperties.PURPOSE_DECRYPT, context)
                .flatMap(keyPair -> defaultProvider.encrypt(unencryptedBytes, keyPair.getPublic()))
                .test()
                .assertNoErrors();
    }

    @Test
    public void encrypt_privateKey$wrongPurpose$defaultProvider_emitsError() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        defaultProvider.generateKeyPair(ALIAS_NEW, KeyProperties.PURPOSE_DECRYPT, context)
                .flatMap(keyPair -> defaultProvider.encrypt(unencryptedBytes, keyPair.getPrivate()))
                .test()
                .assertError(RxEncryptionException.class);
    }

    @Test
    public void encrypt_public$wrongPurpose$bouncyCastleProvider_emitsNoError() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        bouncyCastleProvider.generateKeyPair(ALIAS_NEW, KeyProperties.PURPOSE_DECRYPT, context)
                .flatMap(keyPair -> bouncyCastleProvider.encrypt(unencryptedBytes, keyPair.getPublic()))
                .test()
                .assertNoErrors();
    }

    @Test
    public void encrypt_privateKey$wrongPurpose$bouncyCastleProvider_emitsNoError() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        bouncyCastleProvider.generateKeyPair(ALIAS_NEW, KeyProperties.PURPOSE_DECRYPT, context)
                .flatMap(keyPair -> bouncyCastleProvider.encrypt(unencryptedBytes, keyPair.getPrivate()))
                .test()
                .assertNoErrors();
    }

    @Test
    public void decrypt_publicKey$wrongPurpose$defaultProvider_emitsNoError() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        defaultProvider.generateKeyPair(ALIAS_NEW, KeyProperties.PURPOSE_DECRYPT, context)
                .flatMap(keyPair -> defaultProvider.encrypt(unencryptedBytes, keyPair.getPublic())
                        .flatMap(encryptedBytesAndIV -> defaultProvider.decrypt(encryptedBytesAndIV.first, encryptedBytesAndIV.second, keyPair.getPrivate())))
                .test()
                .assertNoErrors();
    }

    @Test
    public void decrypt_public$wrongPurpose$bouncyCastleProvider_emitsNoError() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        bouncyCastleProvider.generateKeyPair(ALIAS_NEW, KeyProperties.PURPOSE_DECRYPT, context)
                .flatMap(keyPair -> bouncyCastleProvider.encrypt(unencryptedBytes, keyPair.getPublic())
                        .flatMap(encryptedBytesAndIV -> bouncyCastleProvider.decrypt(encryptedBytesAndIV.first, encryptedBytesAndIV.second, keyPair.getPrivate())))
                .test()
                .assertNoErrors();
    }

}
