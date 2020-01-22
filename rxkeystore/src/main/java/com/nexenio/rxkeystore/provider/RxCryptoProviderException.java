package com.nexenio.rxkeystore.provider;

import com.nexenio.rxkeystore.RxKeyStoreException;

public class RxCryptoProviderException extends RxKeyStoreException {

    public RxCryptoProviderException() {
        super();
    }

    public RxCryptoProviderException(String message) {
        super(message);
    }

    public RxCryptoProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public RxCryptoProviderException(Throwable cause) {
        super(cause);
    }

}
