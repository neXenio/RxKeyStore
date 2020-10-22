package com.nexenio.rxkeystore.provider.signature;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.BaseCryptoProviderTest;
import com.nexenio.rxkeystore.provider.RxCryptoProvider;
import com.nexenio.rxkeystore.provider.cipher.asymmetric.RxAsymmetricCipherProvider;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Arrays;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Single;

public abstract class BaseSignatureProviderTest extends BaseCryptoProviderTest {

    protected static final byte[] MESSAGE = LOREM_IPSUM_LONG.getBytes(StandardCharsets.UTF_8);

    protected RxSignatureProvider signatureProvider;
    protected RxAsymmetricCipherProvider asymmetricCipherProvider;
    protected KeyPair firstKeyPair;
    protected KeyPair secondKeyPair;

    @CallSuper
    protected void setUpBeforeEachTest() {
        super.setUpBeforeEachTest();
        asymmetricCipherProvider = createAsymmetricCipherProvider(keyStore);
        firstKeyPair = asymmetricCipherProvider.generateKeyPair("first", context).blockingGet();
        secondKeyPair = asymmetricCipherProvider.generateKeyPair("second", context).blockingGet();
    }

    @Override
    protected RxCryptoProvider createCryptoProvider(@NonNull RxKeyStore keyStore) {
        this.signatureProvider = createSignatureProvider(keyStore);
        return signatureProvider;
    }

    protected abstract RxSignatureProvider createSignatureProvider(@NonNull RxKeyStore keyStore);

    protected abstract RxAsymmetricCipherProvider createAsymmetricCipherProvider(@NonNull RxKeyStore keyStore);

    @Test
    public void sign_validData_emitsSignature() {
        signatureProvider.sign(MESSAGE, firstKeyPair.getPrivate())
                .flatMap(signature -> signatureProvider.getVerificationResult(MESSAGE, signature, firstKeyPair.getPublic()))
                .test()
                .assertValue(true);
    }

    @Test
    public void sign_subsequentCallsWithSameKey_emitsSameSignature() {
        Single<byte[]> signSingle = signatureProvider.sign(MESSAGE, firstKeyPair.getPrivate());

        Single.zip(signSingle, signSingle, Arrays::equals)
                .test()
                .assertValue(true);
    }

    @Test
    public void verify_validSignature_completes() {
        signatureProvider.sign(MESSAGE, firstKeyPair.getPrivate())
                .flatMapCompletable(signature -> signatureProvider.verify(MESSAGE, signature, firstKeyPair.getPublic()))
                .test()
                .assertComplete();
    }

    @Test
    public void verify_invalidSignature_emitsError() {
        byte[] invalidSignature = "This is not valid".getBytes();
        signatureProvider.verify(MESSAGE, invalidSignature, firstKeyPair.getPublic())
                .test()
                .assertError(RxSignatureException.class);
    }

    @Test
    public void verify_validSignatureFromWrongKey_emitsError() {
        signatureProvider.sign(MESSAGE, secondKeyPair.getPrivate())
                .flatMapCompletable(signature -> signatureProvider.verify(MESSAGE, signature, firstKeyPair.getPublic()))
                .test()
                .assertError(RxSignatureException.class);
    }

    @Test
    public void getVerificationResult_validSignature_emitsTrue() {
        signatureProvider.sign(MESSAGE, firstKeyPair.getPrivate())
                .flatMap(signature -> signatureProvider.getVerificationResult(MESSAGE, signature, firstKeyPair.getPublic()))
                .test()
                .assertValue(true);
    }

    @Test
    public void getVerificationResult_invalidSignature_emitsFalse() {
        byte[] invalidSignature = "This is not valid".getBytes();
        signatureProvider.getVerificationResult(MESSAGE, invalidSignature, firstKeyPair.getPublic())
                .test()
                .assertValue(false);
    }

}