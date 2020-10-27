package com.nexenio.rxkeystore.provider.cipher.symmetric;

import android.content.Context;

import com.nexenio.rxkeystore.provider.cipher.RxCipherProvider;

import java.security.KeyStore;

import javax.crypto.SecretKey;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface RxSymmetricCipherProvider extends RxCipherProvider {

    Single<SecretKey> generateKey(@NonNull String alias, @NonNull Context context);

    Single<SecretKey> getKey(@NonNull String alias);

    Maybe<SecretKey> getKeyIfAvailable(@NonNull String alias);

    Completable setKey(@NonNull String alias, @NonNull SecretKey secretKey);

    Completable setKey(@NonNull String alias, @NonNull KeyStore.SecretKeyEntry secretKeyEntry);

}
