package com.nexenio.rxkeystore.provider.asymmetric.ec;

import android.content.Context;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.asymmetric.BaseAsymmetricCryptoProvider;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.ECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Arrays;

import androidx.annotation.NonNull;
import io.reactivex.Single;

import static com.nexenio.rxkeystore.RxKeyStore.BLOCK_MODE_ECB;
import static com.nexenio.rxkeystore.RxKeyStore.DIGEST_SHA256;
import static com.nexenio.rxkeystore.RxKeyStore.KEY_AGREEMENT_ECDH;
import static com.nexenio.rxkeystore.RxKeyStore.KEY_ALGORITHM_EC;

public class RxECCryptoProvider extends BaseAsymmetricCryptoProvider {

    protected static final String CURVE_NAME = "secp256r1";

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
        return Single.fromCallable(() -> new ECGenParameterSpec(CURVE_NAME));
    }

    public String getCurveName() {
        return CURVE_NAME;
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

    public Single<ECPublicKey> getPublicKey(@NonNull ECPoint point) {
        return Single.fromCallable(() -> {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_EC);
            ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec(getCurveName());
            ECParameterSpec namedCurveSpec = new ECNamedCurveSpec(
                    getCurveName(),
                    parameterSpec.getCurve(),
                    parameterSpec.getG(),
                    parameterSpec.getN(),
                    parameterSpec.getH(),
                    parameterSpec.getSeed()
            );
            return (ECPublicKey) keyFactory.generatePublic(new ECPublicKeySpec(point, namedCurveSpec));
        });
    }

    public Single<byte[]> encodePublicKey(@NonNull ECPublicKey publicKey) {
        return encodePoint(publicKey.getW());
    }

    public Single<ECPublicKey> decodePublicKey(@NonNull byte[] bytes) {
        return decodePoint(bytes)
                .flatMap(this::getPublicKey);
    }

    public static Single<byte[]> encodePoint(@NonNull ECPoint point) {
        return Single.fromCallable(() -> {
            byte[] x = point.getAffineX().toByteArray();
            byte[] y = point.getAffineY().toByteArray();
            byte[] encoded = new byte[x.length + y.length];
            System.arraycopy(x, 0, encoded, 0, x.length);
            System.arraycopy(y, 0, encoded, x.length, y.length);
            return encoded;
        });
    }

    public static Single<ECPoint> decodePoint(@NonNull byte[] bytes) {
        return Single.fromCallable(() -> {
            byte[] x = Arrays.copyOfRange(bytes, 0, bytes.length / 2);
            byte[] y = Arrays.copyOfRange(bytes, bytes.length / 2, bytes.length);
            return new ECPoint(new BigInteger(1, x), new BigInteger(1, y));
        });
    }

}
