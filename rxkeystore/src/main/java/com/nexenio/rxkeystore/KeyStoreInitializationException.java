package com.nexenio.rxkeystore;

public class KeyStoreInitializationException extends RxKeyStoreException {

    public KeyStoreInitializationException() {
        super();
    }

    public KeyStoreInitializationException(String message) {
        super(message);
    }

    public KeyStoreInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyStoreInitializationException(Throwable cause) {
        super(cause);
    }

}
