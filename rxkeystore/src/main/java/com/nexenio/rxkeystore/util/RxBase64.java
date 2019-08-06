package com.nexenio.rxkeystore.util;

import android.util.Base64;

import androidx.annotation.NonNull;
import io.reactivex.Single;

public final class RxBase64 {

    private static final int FLAGS_DEFAULT = Base64.DEFAULT;

    public static Single<String> encode(@NonNull byte[] data) {
        return encode(data, FLAGS_DEFAULT);
    }

    public static Single<String> encode(@NonNull byte[] data, int flags) {
        return Single.fromCallable(() -> Base64.encodeToString(data, flags));
    }

    public static Single<byte[]> decode(@NonNull String data) {
        return decode(data, FLAGS_DEFAULT);
    }

    public static Single<byte[]> decode(@NonNull String data, int flags) {
        return Single.fromCallable(() -> Base64.decode(data, flags));
    }

}
