package com.nexenio.rxkeystore;

public class KeyStoreEntryNotAvailableException extends RxKeyStoreException {

    public KeyStoreEntryNotAvailableException() {
    }

    public KeyStoreEntryNotAvailableException(String alias) {
        super("No key store entry available for alias: " + alias);
    }

    public KeyStoreEntryNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyStoreEntryNotAvailableException(Throwable cause) {
        super(cause);
    }

}
