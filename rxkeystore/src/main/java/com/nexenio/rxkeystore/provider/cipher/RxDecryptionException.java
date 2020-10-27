package com.nexenio.rxkeystore.provider.cipher;

public class RxDecryptionException extends RxCipherProviderException {

    private static final String DEFAULT_MESSAGE = "Unable to decrypt data";

    public RxDecryptionException() {
        super(DEFAULT_MESSAGE);
    }

    public RxDecryptionException(String message) {
        super(message);
    }

    public RxDecryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public RxDecryptionException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

}
