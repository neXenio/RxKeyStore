package com.nexenio.rxkeystore.provider.cipher;

import com.nexenio.rxkeystore.provider.RxCryptoProviderException;

public class RxKeyGenerationException extends RxCryptoProviderException {

    public RxKeyGenerationException() {
        super();
    }

    public RxKeyGenerationException(String message) {
        super(message);
    }

    public RxKeyGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RxKeyGenerationException(Throwable cause) {
        super(cause);
    }

}
