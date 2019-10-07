package com.nexenio.rxkeystore.provider.asymmetric.rsa;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import org.junit.Test;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.KeyAgreement;

import static org.junit.Assert.assertArrayEquals;

public class KeyAgreementTest {

    @Test
    public void generateSecretKey_matchingKeyPairs_sameSecretKey() throws Exception {
        KeyPair firstKeyPair = generateKeyPair("first");
        KeyPair secondKeyPair = generateKeyPair("second");

        byte[] firstSecretKey = generateSecretKey(firstKeyPair.getPrivate(), secondKeyPair.getPublic());
        byte[] secondSecretKey = generateSecretKey(secondKeyPair.getPrivate(), firstKeyPair.getPublic());

        assertArrayEquals(firstSecretKey, secondSecretKey);
    }

    private byte[] generateSecretKey(PrivateKey privateKey, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);
        return keyAgreement.generateSecret();
    }

    private KeyPair generateKeyPair(String alias) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        int keyPurposes = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY;
        AlgorithmParameterSpec algorithmParameterSpec = new KeyGenParameterSpec.Builder(alias, keyPurposes)
                .setBlockModes("ECB")
                .setEncryptionPaddings("PKCS1Padding")
                .setSignaturePaddings("PKCS1")
                .setDigests("SHA-256")
                .build();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(algorithmParameterSpec);
        return keyPairGenerator.generateKeyPair();
    }

}