package com.nexenio.rxkeystore.provider;

public class EncryptionException extends RxCryptoProviderException {

    private static final String DEFAULT_MESSAGE = "Unable to encrypt data";

    public EncryptionException() {
        super(DEFAULT_MESSAGE);
    }

    public EncryptionException(String message) {
        super(message);
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncryptionException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

}
