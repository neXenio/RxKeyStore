package com.nexenio.rxkeystore;

import android.content.Context;

import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;
import com.nexenio.rxkeystore.provider.asymmetric.rsa.RxRSACryptoProvider;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Objects;

import androidx.test.platform.app.InstrumentationRegistry;
import io.reactivex.Completable;
import io.reactivex.Single;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

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

    @Ignore("Just for debugging purposes")
    @Test
    public void listSecurityProviders() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Provider provider : Security.getProviders()) {
            stringBuilder.append(String.format("- %s: %s (version %.1f)\n",
                    provider.getName(),
                    provider.getInfo(),
                    provider.getVersion()
            ));
            for (Provider.Service service : provider.getServices()) {
                stringBuilder.append(String.format("\t- %s: %s\n",
                        service.getType(),
                        service.getAlgorithm()
                ));
            }
        }
        System.out.println("Available security providers:\n" + stringBuilder);
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
        assertEquals(RxKeyStore.TYPE_ANDROID, new RxKeyStore().getType());
    }

    @Test
    public void getKeyStoreType_initializedWithType_returnsSpecifiedType() {
        assertEquals(RxKeyStore.TYPE_ANDROID, new RxKeyStore(RxKeyStore.TYPE_ANDROID).getType());
        assertEquals(RxKeyStore.TYPE_BKS, new RxKeyStore(RxKeyStore.TYPE_BKS).getType());
    }

    @Test
    public void getKeyStoreProvider_initializedWithProvider_returnsSpecifiedProvider() {
        assertEquals(RxKeyStore.PROVIDER_ANDROID_KEY_STORE, new RxKeyStore(RxKeyStore.TYPE_ANDROID, RxKeyStore.PROVIDER_ANDROID_KEY_STORE).getProvider());
        assertEquals(RxKeyStore.PROVIDER_BOUNCY_CASTLE, new RxKeyStore(RxKeyStore.TYPE_BKS, RxKeyStore.PROVIDER_BOUNCY_CASTLE).getProvider());
    }

    @Test
    public void shouldUseDefaultProvider_noProviderSpecified_returnsTrue() {
        RxKeyStore rxKeyStore = new RxKeyStore();
        assertTrue(rxKeyStore.shouldUseDefaultProvider());
    }

    @Test
    public void shouldUseDefaultProvider_providerSpecified_returnsFalse() {
        RxKeyStore rxKeyStore = new RxKeyStore(RxKeyStore.TYPE_BKS, RxKeyStore.PROVIDER_BOUNCY_CASTLE);
        assertFalse(rxKeyStore.shouldUseDefaultProvider());
    }

}