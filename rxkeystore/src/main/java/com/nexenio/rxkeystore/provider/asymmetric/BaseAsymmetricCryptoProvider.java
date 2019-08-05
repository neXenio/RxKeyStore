package com.nexenio.rxkeystore.provider.asymmetric;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.BaseCryptoProvider;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public abstract class BaseAsymmetricCryptoProvider extends BaseCryptoProvider implements RxAsymmetricCryptoProvider {

    public BaseAsymmetricCryptoProvider(RxKeyStore rxKeyStore, String keyAlgorithm) {
        super(rxKeyStore, keyAlgorithm);
    }

    @Override
    public Single<byte[]> sign(@NonNull byte[] data, @NonNull PrivateKey privateKey) {
        return getSignatureInstance()
                .map(signature -> {
                    signature.initSign(privateKey);
                    signature.update(data);
                    return signature.sign();
                });
    }

    @Override
    public Completable verify(@NonNull byte[] data, @NonNull byte[] signature, @NonNull PublicKey publicKey) {
        return getVerificationResult(data, signature, publicKey)
                .flatMapCompletable(verificationResult -> Completable.defer(() -> {
                    if (verificationResult) {
                        return Completable.complete();
                    } else {
                        return Completable.error(new SignatureException("Signature verification failed"));
                    }
                }));
    }

    @Override
    public Single<Boolean> getVerificationResult(@NonNull byte[] data, @NonNull byte[] signature, @NonNull PublicKey publicKey) {
        return getSignatureInstance()
                .map(signatureInstance -> {
                    signatureInstance.initVerify(publicKey);
                    signatureInstance.update(data);
                    return signatureInstance.verify(signature);
                });
    }

    @Override
    public Single<KeyPair> generateKeyPair(@NonNull String alias, @NonNull Context context) {
        return getKeyAlgorithmParameterSpec(alias, context)
                .map(algorithmParameterSpec -> {
                    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyAlgorithm, rxKeyStore.getKeyStoreType());
                    keyPairGenerator.initialize(algorithmParameterSpec);
                    return keyPairGenerator.generateKeyPair();
                });
    }

    @Override
    public Single<AlgorithmParameterSpec> getKeyAlgorithmParameterSpec(@NonNull String alias, @NonNull Context context) {
        return Single.defer(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return getKeyGenParameterSpec(alias);
            } else {
                // TODO: 2019-07-26 test this implementation, signature verification didn't work
                return getKeyPairGeneratorSpec(alias, context);
            }
        });
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
                .switchIfEmpty(Single.error(new KeyStoreException("No public and private key pair available with alias: " + alias)));
    }

    @Override
    public Maybe<KeyPair> getKeyPairIfAvailable(@NonNull String alias) {
        return Maybe.zip(getPublicKeyIfAvailable(alias), getPrivateKeyIfAvailable(alias), KeyPair::new);
    }

    protected abstract String getSignatureAlgorithm();

    protected Single<Signature> getSignatureInstance() {
        return Single.defer(() -> Single.just(Signature.getInstance(getSignatureAlgorithm())));
    }

}
