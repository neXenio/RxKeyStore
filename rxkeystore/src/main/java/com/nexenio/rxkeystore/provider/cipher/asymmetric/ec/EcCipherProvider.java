package com.nexenio.rxkeystore.provider.cipher.asymmetric.ec;

import android.content.Context;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.cipher.asymmetric.BaseAsymmetricCipherProvider;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Single;

import static com.nexenio.rxkeystore.RxKeyStore.BLOCK_MODE_ECB;
import static com.nexenio.rxkeystore.RxKeyStore.DIGEST_SHA256;
import static com.nexenio.rxkeystore.RxKeyStore.KEY_AGREEMENT_ECDH;
import static com.nexenio.rxkeystore.RxKeyStore.KEY_ALGORITHM_EC;

public class EcCipherProvider extends BaseAsymmetricCipherProvider {

    private static final String CURVE_NAME = "secp256r1";

    private static final String[] BLOCK_MODES = new String[]{BLOCK_MODE_ECB};
    private static final String[] ENCRYPTION_PADDINGS = new String[]{};
    private static final String[] SIGNATURE_PADDINGS = new String[]{};
    private static final String[] DIGESTS = new String[]{DIGEST_SHA256};

    private static final String TRANSFORMATION_ALGORITHM = "ECIES";
    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";
    private static final String KEY_AGREEMENT_ALGORITHM = KEY_AGREEMENT_ECDH;

    public EcCipherProvider(RxKeyStore rxKeyStore) {
        super(rxKeyStore, KEY_ALGORITHM_EC);
    }

    @Override
    public Single<AlgorithmParameterSpec> getKeyAlgorithmParameterSpec(@NonNull String alias, @NonNull Context context) {
        return Single.fromCallable(() -> new ECGenParameterSpec(CURVE_NAME));
    }

    /**
     * Note: keyPurposes are not being used here.
     */
    @Override
    public Single<AlgorithmParameterSpec> getKeyAlgorithmParameterSpec(@NonNull String alias, int keyPurposes, @NonNull Context context) {
        return getKeyAlgorithmParameterSpec(alias, context);
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
