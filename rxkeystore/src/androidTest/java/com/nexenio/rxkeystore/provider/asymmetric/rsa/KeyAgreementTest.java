package com.nexenio.rxkeystore.provider.asymmetric.rsa;

import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.KeyAgreement;

import static org.junit.Assert.assertArrayEquals;

public class KeyAgreementTest {

    @Test
    public void generateSecretKey_matchingKeyPairs_sameSecretKey() throws Exception {
        KeyPair aliceKeyPair = generateKeyPair("alice");
        KeyPair bobKeyPair = generateKeyPair("bob");

        byte[] firstSecretKey = generateSecret(aliceKeyPair.getPrivate(), bobKeyPair.getPublic());
        byte[] secondSecretKey = generateSecret(bobKeyPair.getPrivate(), aliceKeyPair.getPublic());

        assertArrayEquals(firstSecretKey, secondSecretKey);
    }

    private byte[] generateSecret(PrivateKey privateKey, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);
        return keyAgreement.generateSecret();
    }

    private KeyPair generateKeyPair(String alias) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        keyPairGenerator.initialize(1024);
        return keyPairGenerator.generateKeyPair();
    }

}