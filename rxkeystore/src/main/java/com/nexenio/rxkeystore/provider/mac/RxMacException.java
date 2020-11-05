package com.nexenio.rxkeystore.provider.mac;

import com.nexenio.rxkeystore.provider.RxCryptoProviderException;

public class RxMacException extends RxCryptoProviderException {

    public RxMacException() {
    }

    public RxMacException(String message) {
        super(message);
    }

    public RxMacException(String message, Throwable cause) {
        super(message, cause);
    }

    public RxMacException(Throwable cause) {
        super(cause);
    }

}
