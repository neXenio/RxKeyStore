package com.nexenio.rxkeystore.util;

import java.nio.charset.StandardCharsets;

public final class ByteLoggingUtil {

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    private ByteLoggingUtil() {
        throw new AssertionError("No instantiation allowed");
    }

    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    public static String bytesToBase64(byte[] bytes) {
        return RxBase64.encode(bytes)
                .onErrorReturnItem("Error")
                .blockingGet();
    }

}