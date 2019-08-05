package com.nexenio.rxkeystore.provider.asymmetric.rsa;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.asymmetric.BaseAsymmetricCryptoProvider;

import static com.nexenio.rxkeystore.RxKeyStore.BLOCK_MODE_ECB;
import static com.nexenio.rxkeystore.RxKeyStore.DIGEST_SHA256;
import static com.nexenio.rxkeystore.RxKeyStore.DIGEST_SHA512;
import static com.nexenio.rxkeystore.RxKeyStore.ENCRYPTION_PADDING_RSA_PKCS1;
import static com.nexenio.rxkeystore.RxKeyStore.KEY_ALGORITHM_RSA;
import static com.nexenio.rxkeystore.RxKeyStore.SIGNATURE_PADDING_RSA_PKCS1;

public final class RxRSACryptoProvider extends BaseAsymmetricCryptoProvider {

    private static final String[] BLOCK_MODES = new String[]{BLOCK_MODE_ECB};
    private static final String[] ENCRYPTION_PADDINGS = new String[]{ENCRYPTION_PADDING_RSA_PKCS1};
    private static final String[] SIGNATURE_PADDINGS = new String[]{SIGNATURE_PADDING_RSA_PKCS1};
    private static final String[] DIGESTS = new String[]{DIGEST_SHA256, DIGEST_SHA512};

    private static final String TRANSFORMATION_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    public RxRSACryptoProvider(RxKeyStore rxKeyStore) {
        super(rxKeyStore, KEY_ALGORITHM_RSA);
    }

    @Override
    protected String[] getBlockModes() {
        return BLOCK_MODES;
    }

    @Override
    protected String[] getEncryptionPaddings() {
        return ENCRYPTION_PADDINGS;
    }

    @Override
    protected String[] getSignaturePaddings() {
        return SIGNATURE_PADDINGS;
    }

    @Override
    protected String[] getDigests() {
        return DIGESTS;
    }

    @Override
    protected String getTransformationAlgorithm() {
        return TRANSFORMATION_ALGORITHM;
    }

    @Override
    protected String getSignatureAlgorithm() {
        return SIGNATURE_ALGORITHM;
    }

}
