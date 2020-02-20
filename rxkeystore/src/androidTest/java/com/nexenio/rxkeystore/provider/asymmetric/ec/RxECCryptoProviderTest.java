package com.nexenio.rxkeystore.provider.asymmetric.ec;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.asymmetric.BaseAsymmetricCryptoProviderTest;
import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;
import com.nexenio.rxkeystore.util.RxBase64;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.security.Key;
import java.security.KeyPair;
import java.security.interfaces.ECPublicKey;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

public class RxECCryptoProviderTest extends BaseAsymmetricCryptoProviderTest {

    protected RxECCryptoProvider ecCryptoProvider;

    @CallSuper
    @Before
    @Override
    public void setUpBeforeEachTest() {
        super.setUpBeforeEachTest();
    }

    @Override
    protected RxAsymmetricCryptoProvider createAsymmetricCryptoProvider(@NonNull RxKeyStore keyStore) {
        ecCryptoProvider = new RxECCryptoProvider(keyStore);
        return ecCryptoProvider;
    }

    @Ignore("Unclear if this is the expected behaviour")
    @Test
    @Override
    public void sign_subsequentCallsWithSameKey_emitsSameSignature() {
        super.sign_subsequentCallsWithSameKey_emitsSameSignature();
    }

    @Test
    public void encode() {
        asymmetricCryptoProvider.generateKeyPair(ALIAS_NEW, context)
                .map(KeyPair::getPublic)
                .map(Key::getEncoded)
                .doOnSuccess(bytes -> System.out.println("Encoded length: " + bytes.length))
                .flatMap(RxBase64::encode)
                .doOnSuccess(base64 -> System.out.println("Encoded Base64: " + base64))
                .test()
                .assertComplete();
    }

    @Test
    public void getPublicKey() {
    }

    @Test
    public void encodeAndDecodePublicKey() {
        ECPublicKey originalPublicKey = ecCryptoProvider.generateKeyPair(ALIAS_NEW, context)
                .map(KeyPair::getPublic)
                .cast(ECPublicKey.class)
                .blockingGet();

        ecCryptoProvider.encodePublicKey(originalPublicKey)
                .doOnSuccess(bytes -> {
                    System.out.println("Encoded length: " + bytes.length);
                    System.out.println("Encoded Base64: " + RxBase64.encode(bytes).blockingGet());
                })
                .flatMap(bytes -> ecCryptoProvider.decodePublicKey(bytes))
                .doOnSuccess(ecPublicKey -> {
                    //System.out.println(ecPublicKey.getW().);
                })
                .test()
                .assertValue(decodedPublicKey -> decodedPublicKey.getW().equals(originalPublicKey.getW()));
    }

    @Test
    public void encodePublicKey() {

    }

    @Test
    public void decodePublicKey() {
    }

    @Test
    public void encodePoint() {
    }

    @Test
    public void decodePoint() {
    }

}