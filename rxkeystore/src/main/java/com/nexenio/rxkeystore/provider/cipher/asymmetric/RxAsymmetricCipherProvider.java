package com.nexenio.rxkeystore.provider.cipher.asymmetric;

import android.content.Context;

import com.nexenio.rxkeystore.provider.cipher.RxCipherProvider;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface RxAsymmetricCipherProvider extends RxCipherProvider {

    Single<byte[]> generateSecret(@NonNull PrivateKey privateKey, @NonNull PublicKey publicKey);

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
