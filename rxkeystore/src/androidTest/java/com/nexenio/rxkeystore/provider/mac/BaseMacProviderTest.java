package com.nexenio.rxkeystore.provider.mac;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.BaseCryptoProviderTest;
import com.nexenio.rxkeystore.provider.RxCryptoProvider;
import com.nexenio.rxkeystore.provider.cipher.symmetric.RxSymmetricCipherProvider;
import com.nexenio.rxkeystore.provider.cipher.symmetric.aes.AesCipherProvider;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.crypto.SecretKey;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Single;

public abstract class BaseMacProviderTest extends BaseCryptoProviderTest {

    protected static final byte[] MESSAGE = LOREM_IPSUM_LONG.getBytes(StandardCharsets.UTF_8);

    protected RxMacProvider macProvider;
    protected RxSymmetricCipherProvider symmetricCryptoProvider;
    protected SecretKey firstSecretKey;
    protected SecretKey secondSecretKey;

    @BeforeClass
    public static void setUpBeforeClass() {
        setupSecurityProviders();
    }

    @AfterClass
    public static void cleanUpAfterClass() {
        cleanUpSecurityProviders();
    }

    @CallSuper
    protected void setUpBeforeEachTest() {
        super.setUpBeforeEachTest();
        symmetricCryptoProvider = new AesCipherProvider(keyStore);
        firstSecretKey = symmetricCryptoProvider.generateKey("first", context).blockingGet();
        secondSecretKey = symmetricCryptoProvider.generateKey("second", context).blockingGet();
    }

    @Override
    protected RxCryptoProvider createCryptoProvider(@NonNull RxKeyStore keyStore) {
        this.macProvider = createMacProvider(keyStore);
        return macProvider;
    }

    protected abstract RxMacProvider createMacProvider(@NonNull RxKeyStore keyStore);

    @Test
    public void sign_validData_emitsSignature() {
        macProvider.sign(MESSAGE, firstSecretKey)
                .flatMap(signature -> macProvider.getVerificationResult(MESSAGE, signature, firstSecretKey))
                .test()
                .assertValue(true);
    }

    @Test
    public void sign_subsequentCallsWithSameKey_emitsSameSignature() {
        Single<byte[]> signSingle = macProvider.sign(MESSAGE, firstSecretKey);

        Single.zip(signSingle, signSingle, Arrays::equals)
                .test()
                .assertValue(true);
    }

    @Test
    public void verify_validSignature_completes() {
        macProvider.sign(MESSAGE, firstSecretKey)
                .flatMapCompletable(signature -> macProvider.verify(MESSAGE, signature, firstSecretKey))
                .test()
                .assertComplete();
    }

    @Test
    public void verify_invalidSignature_emitsError() {
        byte[] invalidSignature = "This is not valid".getBytes();
        macProvider.verify(MESSAGE, invalidSignature, firstSecretKey)
                .test()
                .assertError(RxMacException.class);
    }

    @Test
    public void verify_validSignatureFromWrongKey_emitsError() {
        macProvider.sign(MESSAGE, secondSecretKey)
                .flatMapCompletable(signature -> macProvider.verify(MESSAGE, signature, firstSecretKey))
                .test()
                .assertError(RxMacException.class);
    }

    @Test
    public void getVerificationResult_validSignature_emitsTrue() {
        macProvider.sign(MESSAGE, firstSecretKey)
                .flatMap(signature -> macProvider.getVerificationResult(MESSAGE, signature, firstSecretKey))
                .test()
                .assertValue(true);
    }

    @Test
    public void getVerificationResult_invalidSignature_emitsFalse() {
        byte[] invalidSignature = "This is not valid".getBytes();
        macProvider.getVerificationResult(MESSAGE, invalidSignature, firstSecretKey)
                .test()
                .assertValue(false);
    }

}