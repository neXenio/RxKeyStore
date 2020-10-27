package com.nexenio.rxkeystore.provider.signature;

import com.nexenio.rxkeystore.provider.RxCryptoProviderException;

public class RxSignatureException extends RxCryptoProviderException {

    public RxSignatureException() {
        super();
    }

    public RxSignatureException(String message) {
        super(message);
    }

    public RxSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public RxSignatureException(Throwable cause) {
        super(cause);
    }

}
