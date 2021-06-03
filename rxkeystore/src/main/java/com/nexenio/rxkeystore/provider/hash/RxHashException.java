package com.nexenio.rxkeystore.provider.hash;

import com.nexenio.rxkeystore.provider.RxCryptoProviderException;

public class RxHashException extends RxCryptoProviderException {

    public RxHashException() {
    }

    public RxHashException(String message) {
        super(message);
    }

    public RxHashException(String message, Throwable cause) {
        super(message, cause);
    }

    public RxHashException(Throwable cause) {
        super(cause);
    }

}
