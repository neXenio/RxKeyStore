package com.nexenio.rxkeystore.provider.asymmetric.ec;

import android.content.Context;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.asymmetric.BaseAsymmetricCryptoProvider;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;

import androidx.annotation.NonNull;
import io.reactivex.Single;

import static com.nexenio.rxkeystore.RxKeyStore.BLOCK_MODE_ECB;
import static com.nexenio.rxkeystore.RxKeyStore.DIGEST_SHA256;
import static com.nexenio.rxkeystore.RxKeyStore.KEY_AGREEMENT_ECDH;
import static com.nexenio.rxkeystore.RxKeyStore.KEY_ALGORITHM_EC;

public final class RxECCryptoProvider extends BaseAsymmetricCryptoProvider {

    private static final String NAMED_CURVE = "secp256k1";

    private static final String[] BLOCK_MODES = new String[]{BLOCK_MODE_ECB};
    private static final String[] ENCRYPTION_PADDINGS = new String[]{};
    private static final String[] SIGNATURE_PADDINGS = new String[]{};
    private static final String[] DIGESTS = new String[]{DIGEST_SHA256};

    private static final String TRANSFORMATION_ALGORITHM = "ECIES";
    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";
    private static final String KEY_AGREEMENT_ALGORITHM = KEY_AGREEMENT_ECDH;

    public RxECCryptoProvider(RxKeyStore rxKeyStore) {
        super(rxKeyStore, KEY_ALGORITHM_EC);
    }

    @Override
    public Single<AlgorithmParameterSpec> getKeyAlgorithmParameterSpec(@NonNull String alias, @NonNull Context context) {
        return Single.fromCallable(() -> new ECGenParameterSpec(NAMED_CURVE));
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

    @Override
    protected String getKeyAgreementAlgorithm() {
        return KEY_AGREEMENT_ALGORITHM;
    }

}
