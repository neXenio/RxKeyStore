package com.nexenio.rxkeystore.provider.symmetric;

import android.content.Context;

import com.nexenio.rxkeystore.provider.RxCryptoProvider;

import javax.crypto.SecretKey;

import androidx.annotation.NonNull;
import io.reactivex.Maybe;
import io.reactivex.Single;

public interface RxSymmetricCryptoProvider extends RxCryptoProvider {

    Single<SecretKey> generateKey(@NonNull String alias, @NonNull Context context);

    Single<SecretKey> getKey(@NonNull String alias);

    Maybe<SecretKey> getKeyIfAvailable(@NonNull String alias);

}
