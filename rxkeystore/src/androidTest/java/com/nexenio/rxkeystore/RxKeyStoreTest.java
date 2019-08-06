package com.nexenio.rxkeystore;

import android.content.Context;

import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;
import com.nexenio.rxkeystore.provider.asymmetric.rsa.RxRSACryptoProvider;

import org.junit.Before;
import org.junit.Test;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Objects;

import androidx.test.platform.app.InstrumentationRegistry;
import io.reactivex.Completable;
import io.reactivex.Single;

import static junit.framework.TestCase.assertEquals;

public class RxKeyStoreTest {

    private static final String ALIAS_DEFAULT = "default";
    private static final String ALIAS_NEW = "new";

    private Context context;
    private RxKeyStore keyStore;
    private RxAsymmetricCryptoProvider asymmetricCryptoProvider;

    private KeyPair defaultKeyPair;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        keyStore = new RxKeyStore();
        asymmetricCryptoProvider = new RxRSACryptoProvider(keyStore);

        resetKeyStore().andThen(generateDefaultKeyPair())
                .test()
                .assertComplete();
    }

    private Completable resetKeyStore() {
        return keyStore.deleteAllEntries();
    }

    private Completable generateDefaultKeyPair() {
        return asymmetricCryptoProvider.generateKeyPair(ALIAS_DEFAULT, context)
                .doOnSuccess(keyPair -> this.defaultKeyPair = keyPair)
                .ignoreElement();
    }

    @Test
    public void setup_defaultKeyInserted() {
        keyStore.getAliases()
                .test()
                .assertValues(ALIAS_DEFAULT);
    }

    @Test
    public void getLoadedKeyStore_subsequentCalls_emitsSameKeyStore() {
        Single<KeyStore> getLoadedKeyStoreSingle = keyStore.getLoadedKeyStore();
        Single.zip(getLoadedKeyStoreSingle, getLoadedKeyStoreSingle, Objects::equals)
                .test()
                .assertValue(true);
    }

    @Test
    public void getAliases_noneAvailable_completesEmpty() {
        keyStore.deleteAllEntries()
                .andThen(keyStore.getAliases())
                .isEmpty()
                .test()
                .assertValue(true);
    }

    @Test
    public void getAliases_aliasesAvailable_emitsAliases() {
        asymmetricCryptoProvider.generateKeyPair(ALIAS_NEW, context)
                .ignoreElement()
                .andThen(keyStore.getAliases())
                .test()
                .assertValueCount(2); // should contain ALIAS_DEFAULT and ALIAS_NEW
    }

    @Test
    public void getKey_validAlias_emitsKey() {
        keyStore.getKey(ALIAS_DEFAULT)
                .map(Key::getAlgorithm)
                .test()
                .assertValue(defaultKeyPair.getPrivate().getAlgorithm());
    }

    @Test
    public void getKey_invalidAlias_emitsError() {
        keyStore.getKey("asdf")
                .test()
                .assertError(KeyStoreException.class);
    }

    @Test
    public void getKeyIfAvailable_validAlias_emitsKey() {
        keyStore.getKeyIfAvailable(ALIAS_DEFAULT)
                .map(Key::getAlgorithm)
                .test()
                .assertValue(defaultKeyPair.getPrivate().getAlgorithm());
    }

    @Test
    public void getKeyIfAvailable_invalidAlias_completesEmpty() {
        keyStore.getKeyIfAvailable("asdf")
                .test()
                .assertNoValues()
                .assertComplete();
    }

    @Test
    public void getCertificate_validAlias_emitsCertificate() {
        keyStore.getCertificate(ALIAS_DEFAULT)
                .map(Certificate::getPublicKey)
                .test()
                .assertValue(defaultKeyPair.getPublic());
    }

    @Test
    public void getCertificate_invalidAlias_emitsError() {
        keyStore.getCertificate("asdf")
                .test()
                .assertError(KeyStoreException.class);
    }

    @Test
    public void getCertificateIfAvailable_validAlias_emitsCertificate() {
        keyStore.getCertificateIfAvailable(ALIAS_DEFAULT)
                .map(Certificate::getPublicKey)
                .test()
                .assertValue(defaultKeyPair.getPublic());
    }

    @Test
    public void getCertificateIfAvailable_invalidAlias_completesEmpty() {
        keyStore.getCertificateIfAvailable("asdf")
                .test()
                .assertNoValues()
                .assertComplete();
    }

    @Test
    public void deleteAllEntries_entriesAvailable_removesAllEntries() {
        keyStore.deleteAllEntries()
                .andThen(keyStore.getAliases())
                .isEmpty()
                .test()
                .assertValue(true);
    }

    @Test
    public void getKeyStoreType_initializedWithoutType_returnsDefaultType() {
        assertEquals(RxKeyStore.TYPE_ANDROID, new RxKeyStore().getKeyStoreType());
    }

    @Test
    public void getKeyStoreType_initializedWithType_returnsSpecifiedType() {
        assertEquals(RxKeyStore.TYPE_BOUNCY_CASTLE, new RxKeyStore(RxKeyStore.TYPE_BOUNCY_CASTLE).getKeyStoreType());
    }

}