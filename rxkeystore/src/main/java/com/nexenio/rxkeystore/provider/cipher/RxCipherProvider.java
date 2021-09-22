package com.nexenio.rxkeystore.provider.cipher;

import android.content.Context;
import android.util.Pair;

import com.nexenio.rxkeystore.provider.RxCryptoProvider;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.rxjava3.core.Single;

public interface RxCipherProvider extends RxCryptoProvider {

    Single<Pair<byte[], byte[]>> encrypt(@NonNull byte[] data, @NonNull Key key);

    Single<byte[]> encrypt(@NonNull byte[] data, @NonNull byte[] initializationVector, @NonNull Key key);

    Single<byte[]> decrypt(@NonNull byte[] data, @Nullable byte[] initializationVector, @NonNull Key key);

    String getKeyAlgorithm();

    Single<AlgorithmParameterSpec> getKeyAlgorithmParameterSpec(@NonNull String alias, @NonNull Context context);

    Single<AlgorithmParameterSpec> getKeyAlgorithmParameterSpec(@NonNull String alias, int keyPurposes, @NonNull Context context);

}
