package com.nexenio.rxkeystore.provider;

public class KeyGenerationException extends RxCryptoProviderException {

    public KeyGenerationException() {
        super();
    }

    public KeyGenerationException(String message) {
        super(message);
    }

    public KeyGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyGenerationException(Throwable cause) {
        super(cause);
    }

}
