package com.nexenio.rxkeystore.provider;

public class DecryptionException extends RxCryptoProviderException {

    private static final String DEFAULT_MESSAGE = "Unable to decrypt data";

    public DecryptionException() {
        super(DEFAULT_MESSAGE);
    }

    public DecryptionException(String message) {
        super(message);
    }

    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecryptionException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

}
