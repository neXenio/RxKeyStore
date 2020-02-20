package com.nexenio.rxkeystore.provider.asymmetric;

import android.content.Context;

import com.nexenio.rxkeystore.provider.RxCryptoProvider;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public interface RxAsymmetricCryptoProvider extends RxCryptoProvider {

    public static int TEST = 1;

    Single<byte[]> generateSecret(@NonNull PrivateKey privateKey, @NonNull PublicKey publicKey);

    Single<byte[]> sign(@NonNull byte[] data, @NonNull PrivateKey privateKey);

    Completable verify(@NonNull byte[] data, @NonNull byte[] signature, @NonNull PublicKey publicKey);

    Single<Boolean> getVerificationResult(@NonNull byte[] data, @NonNull byte[] signature, @NonNull PublicKey publicKey);

    Single<KeyPair> generateKeyPair(@NonNull String alias, @NonNull Context context);

    Single<KeyPair> getKeyPair(@NonNull String alias);

    Maybe<KeyPair> getKeyPairIfAvailable(@NonNull String alias);

    Single<PrivateKey> getPrivateKey(@NonNull String alias);

    Maybe<PrivateKey> getPrivateKeyIfAvailable(@NonNull String alias);

    Single<PublicKey> getPublicKey(@NonNull String alias);

    Maybe<PublicKey> getPublicKeyIfAvailable(@NonNull String alias);

    Single<Certificate> getCertificate(@NonNull String alias);

    Maybe<Certificate> getCertificateIfAvailable(@NonNull String alias);

    Completable setKeyPair(@NonNull String alias, @NonNull KeyPair keyPair);

    Completable setPrivateKey(@NonNull String alias, @NonNull KeyStore.PrivateKeyEntry privateKeyEntry);

    Completable setCertificate(@NonNull String alias, @NonNull KeyStore.TrustedCertificateEntry trustedCertificateEntry);

}
