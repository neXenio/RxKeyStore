package com.nexenio.rxkeystore;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

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

    public Single<KeyStore> getLoadedKeyStore() {
        return Single.defer(() -> {
            if (keyStore != null) {
                return Single.just(keyStore);
            } else {
                return getLoadedKeyStore(type, provider)
                        .doOnSuccess(loadedKeyStore -> keyStore = loadedKeyStore);
            }
        });
    }

    public Flowable<String> getAliases() {
        return getLoadedKeyStore()
                .map(KeyStore::aliases)
                .map(Collections::list)
                .flatMapPublisher(Flowable::fromIterable);
    }

    public Single<Key> getKey(@NonNull String alias) {
        return getKeyIfAvailable(alias)
                .switchIfEmpty(Single.error(new KeyStoreException("No key available with alias: " + alias)));
    }

    public Maybe<Key> getKeyIfAvailable(@NonNull String alias) {
        return getLoadedKeyStore()
                .flatMapMaybe(keyStore -> Maybe.fromCallable(
                        () -> keyStore.getKey(alias, null)
                ));
    }

    public Single<Certificate> getCertificate(@NonNull String alias) {
        return getCertificateIfAvailable(alias)
                .switchIfEmpty(Single.error(new KeyStoreException("No certificate available with alias: " + alias)));
    }

    public Maybe<Certificate> getCertificateIfAvailable(@NonNull String alias) {
        return getLoadedKeyStore()
                .flatMapMaybe(keyStore -> Maybe.fromCallable(
                        () -> keyStore.getCertificate(alias)
                ));
    }

    private Completable deleteEntry(@NonNull String alias) {
        return getLoadedKeyStore()
                .flatMapCompletable(store -> Completable.fromAction(
                        () -> store.deleteEntry(alias)
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

    private static Single<KeyStore> getLoadedKeyStore(@NonNull String type, @Nullable String provider) {
        return Single.fromCallable(() -> {
            KeyStore keyStore;
            if (provider != null) {
                keyStore = KeyStore.getInstance(type, provider);
            } else {
                keyStore = KeyStore.getInstance(type);
            }
            keyStore.load(null);
            return keyStore;
        });
    }

}
