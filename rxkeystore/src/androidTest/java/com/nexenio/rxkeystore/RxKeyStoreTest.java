package com.nexenio.rxkeystore;

import android.content.Context;

import com.nexenio.rxkeystore.provider.cipher.asymmetric.RxAsymmetricCipherProvider;
import com.nexenio.rxkeystore.provider.cipher.asymmetric.rsa.RsaCipherProvider;
import com.nexenio.rxkeystore.provider.cipher.symmetric.RxSymmetricCipherProvider;
import com.nexenio.rxkeystore.provider.cipher.symmetric.aes.AesCipherProvider;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Objects;

import javax.crypto.SecretKey;

import androidx.test.platform.app.InstrumentationRegistry;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class RxKeyStoreTest {

    private static final String ALIAS_DEFAULT = "default";
    private static final String ALIAS_NEW = "new";

    private static final String KEY_STORE_FILE_NAME = "keys.ks";
    private static final String KEY_STORE_PASSWORD = "password";
    private static final char[] KEY_STORE_PASSWORD_CHARS = KEY_STORE_PASSWORD.toCharArray();

    protected static Provider originalProvider;
    protected static int originalPosition;

    private Context context;
    private RxKeyStore keyStore;
    private RxAsymmetricCipherProvider asymmetricCryptoProvider;

    private KeyPair defaultKeyPair;

    @BeforeClass
    public static void setUpBeforeClass() {
        setupSecurityProviders();
    }

    @AfterClass
    public static void cleanUpAfterClass() {
        cleanUpSecurityProviders();
    }

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        keyStore = new RxKeyStore(RxKeyStore.TYPE_BKS, RxKeyStore.PROVIDER_BOUNCY_CASTLE);
        asymmetricCryptoProvider = new RsaCipherProvider(keyStore);

        resetKeyStore().andThen(generateDefaultKeyPair())
                .test()
                .assertComplete();
    }

    protected static void setupSecurityProviders() {
        Provider[] providers = Security.getProviders();
        for (int i = 0; i < providers.length; i++) {
            Provider provider = providers[i];
            if (BouncyCastleProvider.PROVIDER_NAME.equals(provider.getName())) {
                originalProvider = provider;
                originalPosition = i;
            }
        }
        // originalProvider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (!(originalProvider instanceof BouncyCastleProvider)) {
            // Android registers its own BC provider. As it might be outdated and might not include
            // all needed ciphers, we substitute it with a known BC bundled in the app.
            // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
            // of that it's possible to have another BC implementation loaded in VM.
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
            Security.insertProviderAt(new BouncyCastleProvider(), originalPosition + 1);
        }
    }

    protected static void cleanUpSecurityProviders() {
        if (originalProvider != null) {
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
            Security.insertProviderAt(originalProvider, originalPosition + 1);
        }
    }

    private Completable resetKeyStore() {
        return keyStore.deleteAllEntries();
    }

    private Completable generateDefaultKeyPair() {
        return asymmetricCryptoProvider.generateKeyPair(ALIAS_DEFAULT, context)
                .doOnSuccess(keyPair -> this.defaultKeyPair = keyPair)
                .flatMapCompletable(keyPair -> asymmetricCryptoProvider.setKeyPair(ALIAS_DEFAULT, keyPair));
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
    public void getInitializedKeyStore_subsequentCalls_emitsSameKeyStore() {
        Single<KeyStore> getLoadedKeyStoreSingle = keyStore.getInitializedKeyStore();
        Single.zip(getLoadedKeyStoreSingle, getLoadedKeyStoreSingle, Objects::equals)
                .test()
                .assertValue(true);
    }

    // TODO: 2019-10-22 add test for unsupported types and providers

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
                .flatMapCompletable(keyPair -> asymmetricCryptoProvider.setKeyPair(ALIAS_NEW, keyPair))
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
                .assertError(KeyStoreEntryNotAvailableException.class);
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
                .assertError(KeyStoreEntryNotAvailableException.class);
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

    @Test
    public void load_validStream_loadsKeyStore() throws Exception {
        // create a valid key store file
        RxKeyStore store = new RxKeyStore(RxKeyStore.TYPE_BKS, RxKeyStore.PROVIDER_BOUNCY_CASTLE);
        OutputStream outputStream = context.openFileOutput(KEY_STORE_FILE_NAME, Context.MODE_PRIVATE);
        store.save(outputStream, KEY_STORE_PASSWORD_CHARS).blockingAwait();

        // attempt to load the key store from file
        InputStream inputStream = context.openFileInput(KEY_STORE_FILE_NAME);
        store.load(inputStream, KEY_STORE_PASSWORD_CHARS)
                .test()
                .assertComplete();
    }

    @Test
    public void load_entriesPersisted_entriesRestored() throws Exception {
        // create a key store that can be saved to a file
        RxKeyStore store = new RxKeyStore(RxKeyStore.TYPE_BKS, RxKeyStore.PROVIDER_BOUNCY_CASTLE);
        RxSymmetricCipherProvider cryptoProvider = new AesCipherProvider(store);

        // generate and store a new key pair
        SecretKey secretKey = cryptoProvider.generateKey(ALIAS_NEW, context).blockingGet();
        cryptoProvider.setKey(ALIAS_NEW, secretKey).blockingAwait();

        // create a valid key store file
        OutputStream outputStream = context.openFileOutput(KEY_STORE_FILE_NAME, Context.MODE_PRIVATE);
        store.save(outputStream, KEY_STORE_PASSWORD_CHARS).blockingAwait();

        // load the key store from file
        InputStream inputStream = context.openFileInput(KEY_STORE_FILE_NAME);
        store.load(inputStream, KEY_STORE_PASSWORD_CHARS).blockingAwait();

        // check if the key can be restored
        RxSymmetricCipherProvider restoredCryptoProvider = new AesCipherProvider(store);
        restoredCryptoProvider.getKey(ALIAS_NEW)
                .map(Key::getEncoded)
                .test()
                .assertValue(encodedSecretKey -> Arrays.equals(encodedSecretKey, secretKey.getEncoded()));
    }

    @Test
    public void load_invalidStream_emitsError() throws Exception {
        // create an invalid key store file
        OutputStream outputStream = context.openFileOutput(KEY_STORE_FILE_NAME, Context.MODE_PRIVATE);
        outputStream.write("This is not a valid key store".getBytes());
        outputStream.close();

        // attempt to load the file as key store
        InputStream inputStream = context.openFileInput(KEY_STORE_FILE_NAME);
        RxKeyStore store = new RxKeyStore(RxKeyStore.TYPE_BKS, RxKeyStore.PROVIDER_BOUNCY_CASTLE);
        store.load(inputStream, KEY_STORE_PASSWORD_CHARS)
                .test()
                .assertError(RxKeyStoreException.class);
    }

    @Test
    public void load_invalidPassword_emitsError() throws Exception {
        // create a valid key store file
        RxKeyStore store = new RxKeyStore(RxKeyStore.TYPE_BKS, RxKeyStore.PROVIDER_BOUNCY_CASTLE);
        OutputStream outputStream = context.openFileOutput(KEY_STORE_FILE_NAME, Context.MODE_PRIVATE);
        store.save(outputStream, KEY_STORE_PASSWORD_CHARS).blockingAwait();

        // attempt to load the key store with a different password
        InputStream inputStream = context.openFileInput(KEY_STORE_FILE_NAME);
        store.load(inputStream, "wrong password".toCharArray())
                .test()
                .assertError(RxKeyStoreException.class);
    }

    @Test
    public void save_serializableKeyStore_savesKeyStore() throws Exception {
        RxKeyStore store = new RxKeyStore(RxKeyStore.TYPE_BKS, RxKeyStore.PROVIDER_BOUNCY_CASTLE);
        OutputStream stream = context.openFileOutput(KEY_STORE_FILE_NAME, Context.MODE_PRIVATE);
        store.save(stream, KEY_STORE_PASSWORD_CHARS)
                .test()
                .assertComplete();
    }

    @Test
    public void save_nonSerializableKeyStore_emitsError() throws Exception {
        RxKeyStore store = new RxKeyStore(); // defaults to the Android key store
        OutputStream stream = context.openFileOutput(KEY_STORE_FILE_NAME, Context.MODE_PRIVATE);
        store.save(stream, KEY_STORE_PASSWORD_CHARS)
                .test()
                .assertError(RxKeyStoreException.class);
    }

}