package com.nexenio.rxkeystore.provider.cipher.asymmetric;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.nexenio.rxkeystore.KeyStoreEntryNotAvailableException;
import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.cipher.BaseCipherProvider;
import com.nexenio.rxkeystore.provider.cipher.RxCipherProviderException;
import com.nexenio.rxkeystore.provider.cipher.RxKeyGenerationException;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcContentSignerBuilder;
import org.bouncycastle.operator.bc.BcECContentSignerBuilder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;
import java.util.Locale;

import javax.crypto.KeyAgreement;
import javax.security.auth.x500.X500Principal;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

import static com.nexenio.rxkeystore.RxKeyStore.PROVIDER_ANDROID_KEY_STORE;

public abstract class BaseAsymmetricCipherProvider extends BaseCipherProvider implements RxAsymmetricCipherProvider {

    public BaseAsymmetricCipherProvider(RxKeyStore rxKeyStore, String keyAlgorithm) {
        super(rxKeyStore, keyAlgorithm);
    }

    @Override
    public Single<byte[]> generateSecret(@NonNull PrivateKey privateKey, @NonNull PublicKey publicKey) {
        return getKeyAgreementInstance()
                .flatMap(keyAgreement -> Single.fromCallable(() -> {
                    byte[] secret;
                    synchronized (keyAgreement) {
                        keyAgreement.init(privateKey);
                        keyAgreement.doPhase(publicKey, true);
                        secret = keyAgreement.generateSecret();
                    }
                    return secret;
                }))
                .onErrorResumeNext(throwable -> Single.error(
                        new RxKeyGenerationException("Unable to generate secret", throwable)
                ));
    }

    @Override
    public Single<KeyPair> generateKeyPair(@NonNull String alias, @NonNull Context context) {
        return Single.defer(() -> {
            Single<KeyPair> keyPairGenerationSingle = getKeyPairGeneratorInstance()
                    .flatMap(keyPairGenerator -> generateKeyPair(alias, context, keyPairGenerator))
                    .onErrorResumeNext(throwable -> Single.error(
                            new RxKeyGenerationException("Unable to generate key pair", throwable)
                    ));
            if (PROVIDER_ANDROID_KEY_STORE.equals(rxKeyStore.getProvider()) && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                // Work-around for incorrect handling of languages that go from right to left.
                // See https://github.com/AzureAD/azure-activedirectory-library-for-android/wiki/Common-Issues-With-AndroidKeyStore for more information
                // See https://github.com/marcin-adamczewski/media-cipher/blob/master/library/src/main/java/com/appunite/mediacipher/crypto/AESCrypterBelowM.java
                return Single.just(Locale.getDefault())
                        .flatMap(defaultLocale -> keyPairGenerationSingle
                                .doOnSubscribe(disposable -> Locale.setDefault(Locale.ENGLISH))
                                .doFinally(() -> Locale.setDefault(defaultLocale)));
            } else {
                return keyPairGenerationSingle;
            }
        });
    }

    protected Single<KeyPair> generateKeyPair(@NonNull String alias, @NonNull Context context, @NonNull KeyPairGenerator keyPairGenerator) {
        return getKeyAlgorithmParameterSpec(alias, context)
                .map(algorithmParameterSpec -> {
                    KeyPair keyPair;
                    synchronized (keyPairGenerator) {
                        keyPairGenerator.initialize(algorithmParameterSpec);
                        keyPair = keyPairGenerator.generateKeyPair();
                    }
                    return keyPair;
                });
    }

    @Override
    public Single<AlgorithmParameterSpec> getKeyAlgorithmParameterSpec(@NonNull String alias, @NonNull Context context) {
        return rxKeyStore.checkIfStrongBoxIsSupported(context)
                .andThen(Single.defer(() -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        return getKeyGenParameterSpec(alias);
                    } else {
                        // TODO: 2019-07-26 test this implementation, signature verification didn't work
                        return getKeyPairGeneratorSpec(alias, context);
                    }
                }));
    }

    protected Single<KeyPairGeneratorSpec> getKeyPairGeneratorSpec(@NonNull String alias, @NonNull Context context) {
        return Single.fromCallable(() -> {
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.YEAR, 10);

            KeyPairGeneratorSpec.Builder builder = new KeyPairGeneratorSpec.Builder(context)
                    .setAlias(alias)
                    .setSerialNumber(BigInteger.ONE)
                    .setSubject(new X500Principal("CN=" + alias + " CA Certificate"))
                    .setStartDate(startDate.getTime())
                    .setEndDate(endDate.getTime());

            return builder.build();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected Single<KeyGenParameterSpec> getKeyGenParameterSpec(@NonNull String alias) {
        return Single.fromCallable(() -> {
            int keyPurposes = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY;
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(alias, keyPurposes)
                    .setBlockModes(getBlockModes())
                    .setEncryptionPaddings(getEncryptionPaddings())
                    .setSignaturePaddings(getSignaturePaddings())
                    .setDigests(getDigests());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                builder.setIsStrongBoxBacked(shouldUseStrongBox());
            }

            return builder.build();
        });
    }

    protected abstract String[] getBlockModes();

    protected abstract String[] getEncryptionPaddings();

    protected abstract String[] getSignaturePaddings();

    protected abstract String[] getDigests();

    @Override
    public Single<PrivateKey> getPrivateKey(@NonNull String alias) {
        return rxKeyStore.getKey(alias).cast(PrivateKey.class);
    }

    @Override
    public Maybe<PrivateKey> getPrivateKeyIfAvailable(@NonNull String alias) {
        return rxKeyStore.getKeyIfAvailable(alias).cast(PrivateKey.class);
    }

    @Override
    public Single<PublicKey> getPublicKey(@NonNull String alias) {
        return getCertificate(alias).map(Certificate::getPublicKey);
    }

    @Override
    public Maybe<PublicKey> getPublicKeyIfAvailable(@NonNull String alias) {
        return getCertificateIfAvailable(alias).map(Certificate::getPublicKey);
    }

    @Override
    public Single<Certificate> getCertificate(@NonNull String alias) {
        return rxKeyStore.getCertificate(alias);
    }

    @Override
    public Maybe<Certificate> getCertificateIfAvailable(@NonNull String alias) {
        return rxKeyStore.getCertificateIfAvailable(alias);
    }

    @Override
    public Single<KeyPair> getKeyPair(@NonNull String alias) {
        return getKeyPairIfAvailable(alias)
                .switchIfEmpty(Single.error(new KeyStoreEntryNotAvailableException(alias)));
    }

    @Override
    public Maybe<KeyPair> getKeyPairIfAvailable(@NonNull String alias) {
        return Maybe.zip(getPublicKeyIfAvailable(alias), getPrivateKeyIfAvailable(alias), KeyPair::new);
    }

    @Override
    public Completable setKeyPair(@NonNull String alias, @NonNull KeyPair keyPair) {
        return createSelfSignedCertificate(keyPair)
                .map(certificate -> new Certificate[]{certificate})
                .map(certificateChain -> new KeyStore.PrivateKeyEntry(keyPair.getPrivate(), certificateChain))
                .flatMapCompletable(privateKeyEntry -> setPrivateKey(alias, privateKeyEntry));
    }

    @Override
    public Completable setPrivateKey(@NonNull String alias, @NonNull KeyStore.PrivateKeyEntry privateKeyEntry) {
        return rxKeyStore.setEntry(alias, privateKeyEntry);
    }

    @Override
    public Completable setCertificate(@NonNull String alias, @NonNull KeyStore.TrustedCertificateEntry trustedCertificateEntry) {
        return rxKeyStore.setEntry(alias, trustedCertificateEntry);
    }

    protected Single<Certificate> createSelfSignedCertificate(@NonNull KeyPair keyPair) {
        return createContentSigner(keyPair.getPrivate())
                .flatMap(contentSigner -> Single.fromCallable(() -> {
                    Calendar startDate = Calendar.getInstance();
                    Calendar endDate = Calendar.getInstance();
                    endDate.add(Calendar.YEAR, 10);

                    SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());

                    X500NameBuilder nameBuilder = new X500NameBuilder(X500Name.getDefaultStyle());
                    nameBuilder.addRDN(BCStyle.CN, "RxKeyStore");
                    nameBuilder.addRDN(BCStyle.OU, "Cryptography");
                    nameBuilder.addRDN(BCStyle.O, "neXenio");
                    nameBuilder.addRDN(BCStyle.L, "Berlin");
                    nameBuilder.addRDN(BCStyle.ST, "Berlin");
                    nameBuilder.addRDN(BCStyle.C, "DE");

                    X500Name issuer = nameBuilder.build();
                    X500Name subject = nameBuilder.build();

                    BigInteger serialNumber = new BigInteger(64, new SecureRandom());

                    X509v1CertificateBuilder certificateBuilder = new X509v1CertificateBuilder(
                            issuer,
                            serialNumber,
                            startDate.getTime(),
                            endDate.getTime(),
                            subject,
                            keyInfo
                    );

                    JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();
                    if (!rxKeyStore.shouldUseDefaultProvider()) {
                        certificateConverter.setProvider(rxKeyStore.getProvider());
                    }

                    X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);
                    return certificateConverter.getCertificate(certificateHolder);
                }));
    }

    protected Single<ContentSigner> createContentSigner(@NonNull PrivateKey privateKey) {
        return Single.defer(() -> {
            AsymmetricKeyParameter keyParameter = PrivateKeyFactory.createKey(privateKey.getEncoded());

            AlgorithmIdentifier signatureId = new DefaultSignatureAlgorithmIdentifierFinder().find(getSignatureAlgorithm());
            AlgorithmIdentifier digestId = new DefaultDigestAlgorithmIdentifierFinder().find(signatureId);

            BcContentSignerBuilder contentSignerBuilder = null;

            switch (privateKey.getAlgorithm()) {
                case RxKeyStore.KEY_ALGORITHM_RSA:
                    contentSignerBuilder = new BcRSAContentSignerBuilder(signatureId, digestId);
                    break;
                case RxKeyStore.KEY_ALGORITHM_EC:
                    contentSignerBuilder = new BcECContentSignerBuilder(signatureId, digestId);
                    break;
            }

            if (contentSignerBuilder == null) {
                return Single.error(new IllegalArgumentException("Unable to create content signer for key algorithm: " + privateKey.getAlgorithm()));
            }

            return Single.just(contentSignerBuilder.build(keyParameter));
        });
    }

    /**
     * Note: {@link KeyAgreement} instances are not thread safe!
     */
    protected Single<KeyPairGenerator> getKeyPairGeneratorInstance() {
        return Single.defer(() -> {
            KeyPairGenerator keyPairGenerator;
            if (rxKeyStore.shouldUseDefaultProvider()) {
                keyPairGenerator = KeyPairGenerator.getInstance(getKeyAlgorithm());
            } else {
                keyPairGenerator = KeyPairGenerator.getInstance(getKeyAlgorithm(), rxKeyStore.getProvider());
            }
            return Single.just(keyPairGenerator);
        });
    }

    protected abstract String getKeyAgreementAlgorithm();

    /**
     * Note: {@link KeyAgreement} instances are not thread safe!
     */
    protected Single<KeyAgreement> getKeyAgreementInstance() {
        return Single.defer(() -> {
            KeyAgreement keyAgreement;
            if (rxKeyStore.shouldUseDefaultProvider()) {
                keyAgreement = KeyAgreement.getInstance(getKeyAgreementAlgorithm());
            } else {
                keyAgreement = KeyAgreement.getInstance(getKeyAgreementAlgorithm(), rxKeyStore.getProvider());
            }
            return Single.just(keyAgreement);
        }).onErrorResumeNext(throwable -> Single.error(
                new RxCipherProviderException("Unable to get Key Agreement instance: " + getKeyAgreementAlgorithm(), throwable)
        ));
    }

    protected abstract String getSignatureAlgorithm();

}
