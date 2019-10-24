package com.nexenio.rxkeystore.provider.asymmetric;

import com.nexenio.rxkeystore.provider.RxCryptoProviderException;

public class SignatureException extends RxCryptoProviderException {

    public SignatureException() {
        super();
    }

    public SignatureException(String message) {
        super(message);
    }

    public SignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignatureException(Throwable cause) {
        super(cause);
    }

}
