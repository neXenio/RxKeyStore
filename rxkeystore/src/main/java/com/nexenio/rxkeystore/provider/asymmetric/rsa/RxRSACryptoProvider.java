package com.nexenio.rxkeystore.provider.asymmetric.rsa;

import android.content.Context;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.asymmetric.BaseAsymmetricCryptoProvider;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Single;

import static com.nexenio.rxkeystore.RxKeyStore.BLOCK_MODE_ECB;
import static com.nexenio.rxkeystore.RxKeyStore.DIGEST_SHA256;
import static com.nexenio.rxkeystore.RxKeyStore.DIGEST_SHA512;
import static com.nexenio.rxkeystore.RxKeyStore.ENCRYPTION_PADDING_RSA_PKCS1;
import static com.nexenio.rxkeystore.RxKeyStore.KEY_AGREEMENT_DH;
import static com.nexenio.rxkeystore.RxKeyStore.KEY_ALGORITHM_RSA;
import static com.nexenio.rxkeystore.RxKeyStore.SIGNATURE_PADDING_RSA_PKCS1;

public class RxRSACryptoProvider extends BaseAsymmetricCryptoProvider {

    private static final int KEY_SIZE = 2048;
    private static final String[] BLOCK_MODES = new String[]{BLOCK_MODE_ECB};
    private static final String[] ENCRYPTION_PADDINGS = new String[]{ENCRYPTION_PADDING_RSA_PKCS1};
    private static final String[] SIGNATURE_PADDINGS = new String[]{SIGNATURE_PADDING_RSA_PKCS1};
    private static final String[] DIGESTS = new String[]{DIGEST_SHA256, DIGEST_SHA512};

    private static final String TRANSFORMATION_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String KEY_AGREEMENT_ALGORITHM = KEY_AGREEMENT_DH;

    public RxRSACryptoProvider(RxKeyStore rxKeyStore) {
        super(rxKeyStore, KEY_ALGORITHM_RSA);
    }

    @Override
    public Single<AlgorithmParameterSpec> getKeyAlgorithmParameterSpec(@NonNull String alias, @NonNull Context context) {
        return Single.defer(() -> {
            if (RxKeyStore.PROVIDER_BOUNCY_CASTLE.equals(rxKeyStore.getProvider())) {
                return Single.fromCallable(() -> new RSAKeyGenParameterSpec(KEY_SIZE, RSAKeyGenParameterSpec.F4));
            } else {
                return super.getKeyAlgorithmParameterSpec(alias, context);
            }
        });
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
