package com.nexenio.rxkeystore.provider.asymmetric.ec;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.asymmetric.BaseAsymmetricCryptoProviderTest;
import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;

import androidx.annotation.NonNull;

public class RxECCryptoProviderTest extends BaseAsymmetricCryptoProviderTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Override
    protected RxKeyStore createKeyStore() {
        //return super.createKeyStore();
        setupBouncyCastle();
        return new RxKeyStore(RxKeyStore.TYPE_BKS, RxKeyStore.PROVIDER_BOUNCY_CASTLE);
    }

    private void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (!(provider instanceof BouncyCastleProvider)) {
            // Android registers its own BC provider. As it might be outdated and might not include
            // all needed ciphers, we substitute it with a known BC bundled in the app.
            // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
            // of that it's possible to have another BC implementation loaded in VM.
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
            Security.insertProviderAt(new BouncyCastleProvider(), 1);
        }
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
    public void generate() throws Exception {
        ECGenParameterSpec ecGenSpec = new ECGenParameterSpec("prime192v1");
        KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "BC");
        g.initialize(ecGenSpec, new SecureRandom());
        KeyPair pair = g.generateKeyPair();
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