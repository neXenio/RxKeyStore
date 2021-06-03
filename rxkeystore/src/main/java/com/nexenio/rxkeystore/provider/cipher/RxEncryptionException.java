package com.nexenio.rxkeystore.provider.cipher;

public class RxEncryptionException extends RxCipherProviderException {

    private static final String DEFAULT_MESSAGE = "Unable to encrypt data";

    public RxEncryptionException() {
        super(DEFAULT_MESSAGE);
    }

    public RxEncryptionException(String message) {
        super(message);
    }

    public RxEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public RxEncryptionException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

}
