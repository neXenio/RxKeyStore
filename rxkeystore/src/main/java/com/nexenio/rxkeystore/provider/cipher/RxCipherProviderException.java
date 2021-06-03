package com.nexenio.rxkeystore.provider.cipher;

import com.nexenio.rxkeystore.provider.RxCryptoProviderException;

public class RxCipherProviderException extends RxCryptoProviderException {

    public RxCipherProviderException() {
    }

    public RxCipherProviderException(String message) {
        super(message);
    }

    public RxCipherProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public RxCipherProviderException(Throwable cause) {
        super(cause);
    }

}
