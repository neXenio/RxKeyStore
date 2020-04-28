package com.nexenio.rxkeystore;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public final class RxKeyStore {

    public static final String PROVIDER_ANDROID_OPEN_SSL = "AndroidOpenSSL";
    public static final String PROVIDER_ANDROID_KEY_STORE = "AndroidKeyStore";
    public static final String PROVIDER_BOUNCY_CASTLE = "BC";

    public static final String TYPE_ANDROID = "AndroidKeyStore";
    public static final String TYPE_JKS = "JKS";
    public static final String TYPE_BKS = "BKS";

    public static final String KEY_ALGORITHM_RSA = "RSA";
    public static final String KEY_ALGORITHM_EC = "EC";

    public static final String KEY_ALGORITHM_AES = "AES";

    public static final String TRANSFORMATION_RSA = "RSA/ECB/PKCS1Padding";
    public static final String TRANSFORMATION_AES = "AES/CBC/PKCS7Padding";

    public static final String SIGNATURE_ALGORITHM_RSA = "SHA256withRSA";
    public static final String SIGNATURE_ALGORITHM_EC = "SHA256withECDSA";

    public static final String BLOCK_MODE_ECB = "ECB";
    public static final String BLOCK_MODE_CBC = "CBC";
    public static final String BLOCK_MODE_CTR = "CTR";
    public static final String BLOCK_MODE_GCM = "GCM";

    public static final String ENCRYPTION_PADDING_NONE = "NoPadding";
    public static final String ENCRYPTION_PADDING_PKCS7 = "PKCS7Padding";
    public static final String ENCRYPTION_PADDING_RSA_PKCS1 = "PKCS1Padding";
    public static final String ENCRYPTION_PADDING_RSA_OAEP = "OAEPPadding";

    public static final String SIGNATURE_PADDING_RSA_PKCS1 = "PKCS1";
    public static final String SIGNATURE_PADDING_RSA_PSS = "PSS";

    public static final String DIGEST_NONE = "NONE";
    public static final String DIGEST_MD5 = "MD5";
    public static final String DIGEST_SHA1 = "SHA-1";
    public static final String DIGEST_SHA224 = "SHA-224";
    public static final String DIGEST_SHA256 = "SHA-256";
    public static final String DIGEST_SHA384 = "SHA-384";
    public static final String DIGEST_SHA512 = "SHA-512";

    public static final String CERTIFICATE_TYPE_X509 = "X.509";

    public static final String KEY_AGREEMENT_DH = "DH";
    public static final String KEY_AGREEMENT_ECDH = "ECDH";

    private final String type;

    @Nullable
    private final String provider;

    private KeyStore keyStore;

    public RxKeyStore() {
        this(TYPE_ANDROID);
    }

    public RxKeyStore(@NonNull String type) {
        this(type, null);
    }

    public RxKeyStore(@NonNull String type, @Nullable String provider) {
        this.type = type;
        this.provider = provider;
    }

    public Single<KeyStore> getInitializedKeyStore() {
        return Single.defer(() -> {
            if (keyStore != null) {
                return Single.just(keyStore);
            } else {
                return getInitializedKeyStore(type, provider)
                        .doOnSuccess(initializedKeyStore -> keyStore = initializedKeyStore);
            }
        });
    }

    /**
     * Loads this key store from the given input stream.
     *
     * A password may be given to unlock the key store (e.g. the keystore resides on a hardware
     * token device), or to check the integrity of the key store data.
     *
     * Note that if this key store has already been loaded, it is reinitialized and loaded again
     * from the given input stream.
     *
     * @param stream   the input stream from which the keystore is loaded
     * @param password the password used to check the integrity of the key store, the password used
     *                 to unlock the keystore, or {@code null}
     */
    public Completable load(@NonNull InputStream stream, @Nullable String password) {
        return getInitializedKeyStore()
                .flatMapCompletable(initializedKeyStore -> Completable.fromAction(() -> {
                    char[] passwordChars = password != null ? password.toCharArray() : null;
                    initializedKeyStore.load(stream, passwordChars);
                })).onErrorResumeNext(throwable -> Completable.error(
                        new RxKeyStoreException("Unable to load key store", throwable)
                ));
    }

    /**
     * Stores this key store to the given output stream, and protects its integrity with the given
     * password.
     *
     * @param stream   the output stream to which this keystore is written.
     * @param password the password to generate the keystore integrity check
     */
    public Completable save(@NonNull OutputStream stream, @Nullable String password) {
        return getInitializedKeyStore()
                .flatMapCompletable(initializedKeyStore -> Completable.fromAction(() -> {
                    char[] passwordChars = password != null ? password.toCharArray() : null;
                    initializedKeyStore.store(stream, passwordChars);
                })).onErrorResumeNext(throwable -> Completable.error(
                        new RxKeyStoreException("Unable to save key store", throwable)
                ));
    }

    public Flowable<String> getAliases() {
        return getInitializedKeyStore()
                .map(KeyStore::aliases)
                .map(Collections::list)
                .flatMapPublisher(Flowable::fromIterable);
    }

    public Single<Key> getKey(@NonNull String alias) {
        return getKeyIfAvailable(alias)
                .switchIfEmpty(Single.error(new KeyStoreEntryNotAvailableException(alias)));
    }

    public Maybe<Key> getKeyIfAvailable(@NonNull String alias) {
        return getInitializedKeyStore()
                .flatMapMaybe(keyStore -> Maybe.fromCallable(
                        () -> keyStore.getKey(alias, null)
                ));
    }

    public Single<Certificate> getCertificate(@NonNull String alias) {
        return getCertificateIfAvailable(alias)
                .switchIfEmpty(Single.error(new KeyStoreEntryNotAvailableException(alias)));
    }

    public Maybe<Certificate> getCertificateIfAvailable(@NonNull String alias) {
        return getInitializedKeyStore()
                .flatMapMaybe(keyStore -> Maybe.fromCallable(
                        () -> keyStore.getCertificate(alias)
                ));
    }

    public Completable setEntry(@NonNull String alias, @NonNull KeyStore.Entry entry) {
        return setEntry(alias, entry, null);
    }

    public Completable setEntry(@NonNull String alias, @NonNull KeyStore.Entry entry, @Nullable KeyStore.ProtectionParameter protectionParameter) {
        return getInitializedKeyStore()
                .flatMapCompletable(store -> Completable.fromAction(
                        () -> store.setEntry(alias, entry, protectionParameter)
                )).onErrorResumeNext(throwable -> Completable.error(
                        new RxKeyStoreException("Unable to set key store entry", throwable)
                ));
    }

    public Completable deleteEntry(@NonNull String alias) {
        return getInitializedKeyStore()
                .flatMapCompletable(store -> Completable.fromAction(
                        () -> store.deleteEntry(alias)
                )).onErrorResumeNext(throwable -> Completable.error(
                        new RxKeyStoreException("Unable to delete key store entry", throwable)
                ));
    }

    public Completable deleteAllEntries() {
        return getAliases()
                .flatMapCompletable(this::deleteEntry);
    }

    public String getType() {
        return type;
    }

    @Nullable
    public String getProvider() {
        return provider;
    }

    public boolean shouldUseDefaultProvider() {
        return provider == null;
    }

    private static Single<KeyStore> getInitializedKeyStore(@NonNull String type, @Nullable String provider) {
        return Single.fromCallable(() -> {
            KeyStore keyStore;
            if (provider != null) {
                keyStore = KeyStore.getInstance(type, provider);
            } else {
                keyStore = KeyStore.getInstance(type);
            }
            keyStore.load(null);
            return keyStore;
        }).onErrorResumeNext(throwable -> Single.error(
                new KeyStoreInitializationException("Unable to initialize keystore", throwable)
        ));
    }

}
