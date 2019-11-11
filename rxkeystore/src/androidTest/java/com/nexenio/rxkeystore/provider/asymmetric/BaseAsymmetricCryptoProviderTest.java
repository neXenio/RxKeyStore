package com.nexenio.rxkeystore.provider.asymmetric;

import android.util.Base64;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.BaseCryptoProviderTest;
import com.nexenio.rxkeystore.provider.DecryptionException;
import com.nexenio.rxkeystore.provider.RxCryptoProvider;

import org.junit.Ignore;
import org.junit.Test;

import java.security.Key;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Single;

@Ignore("Abstract base class for tests only")
public abstract class BaseAsymmetricCryptoProviderTest extends BaseCryptoProviderTest {

    protected RxAsymmetricCryptoProvider asymmetricCryptoProvider;

    @Override
    protected RxCryptoProvider createCryptoProvider(@NonNull RxKeyStore keyStore) {
        this.asymmetricCryptoProvider = createAsymmetricCryptoProvider(keyStore);
        return asymmetricCryptoProvider;
    }

    protected abstract RxAsymmetricCryptoProvider createAsymmetricCryptoProvider(@NonNull RxKeyStore keyStore);

    @Override
    protected Completable generateDefaultKeys() {
        return asymmetricCryptoProvider.generateKeyPair(ALIAS_DEFAULT, context)
                .flatMapCompletable(keyPair -> asymmetricCryptoProvider.setKeyPair(ALIAS_DEFAULT, keyPair));
    }

    @Test
    public void encrypt_validData_emitsEncryptedData() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        asymmetricCryptoProvider.generateKeyPair(ALIAS_NEW, context)
                .flatMap(keyPair -> asymmetricCryptoProvider.encrypt(unencryptedBytes, keyPair.getPublic())
                        .flatMap(encryptedBytesAndIV -> asymmetricCryptoProvider.decrypt(encryptedBytesAndIV.first, encryptedBytesAndIV.second, keyPair.getPrivate())))
                .test()
                .assertValue(decryptedBytes -> Arrays.equals(unencryptedBytes, decryptedBytes));
    }

    /**
     * Subsequent calls to {@link RxAsymmetricCryptoProvider#encrypt(byte[], Key)} should emit
     * distinct data for the same input, even if the same key was used. If the same data is emitted,
     * that indicates a misuse of a static initialization vector or a bad {@link SecureRandom}
     * provider.
     */
    @Test
    public void encrypt_subsequentCallsWithSameKey_emitsDistinctData() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        Single<byte[]> getEncryptedBytesSingle = asymmetricCryptoProvider.getPublicKey(ALIAS_DEFAULT)
                .flatMap(publicKey -> asymmetricCryptoProvider.encrypt(unencryptedBytes, publicKey))
                .map(pair -> pair.first);

        Single.zip(getEncryptedBytesSingle, getEncryptedBytesSingle, Arrays::equals)
                .test()
                .assertValue(false);
    }

    @Test
    public void decrypt_validData_emitsDecryptedData() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        asymmetricCryptoProvider.generateKeyPair(ALIAS_NEW, context)
                .flatMap(keyPair -> asymmetricCryptoProvider.encrypt(unencryptedBytes, keyPair.getPublic())
                        .flatMap(encryptedBytesAndIV -> asymmetricCryptoProvider.decrypt(encryptedBytesAndIV.first, encryptedBytesAndIV.second, keyPair.getPrivate())))
                .test()
                .assertValue(decryptedBytes -> Arrays.equals(unencryptedBytes, decryptedBytes));
    }

    @Test
    public void decrypt_invalidData_emitsError() {
        byte[] unencryptedBytes = new byte[]{};

        asymmetricCryptoProvider.generateKeyPair(ALIAS_NEW, context)
                .flatMap(keyPair -> asymmetricCryptoProvider.decrypt(unencryptedBytes, null, keyPair.getPrivate()))
                .test()
                .assertError(DecryptionException.class);
    }

    @Test
    public void generateSecretKey_matchingKeyPairs_sameSecretKey() {
        KeyPair firstKeyPair = asymmetricCryptoProvider.getKeyPair(ALIAS_DEFAULT).blockingGet();
        KeyPair secondKeyPair = asymmetricCryptoProvider.generateKeyPair(ALIAS_NEW, context).blockingGet();

        Single<byte[]> firstSecretSingle = asymmetricCryptoProvider.generateSecret(firstKeyPair.getPrivate(), secondKeyPair.getPublic());
        Single<byte[]> secondSecretSingle = asymmetricCryptoProvider.generateSecret(secondKeyPair.getPrivate(), firstKeyPair.getPublic());

        Single.zip(firstSecretSingle, secondSecretSingle, Arrays::equals)
                .test()
                .assertValue(true);
    }

    @Test
    public void sign_validData_emitsSignature() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        asymmetricCryptoProvider.generateKeyPair(ALIAS_NEW, context)
                .flatMap(keyPair -> asymmetricCryptoProvider.sign(unencryptedBytes, keyPair.getPrivate())
                        .flatMap(signedBuffer -> asymmetricCryptoProvider.getVerificationResult(unencryptedBytes, signedBuffer, keyPair.getPublic())))
                .test()
                .assertValue(true);
    }

    @Test
    public void sign_subsequentCallsWithSameKey_emitsSameSignature() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        Single<byte[]> signSingle = asymmetricCryptoProvider.getPrivateKey(ALIAS_DEFAULT)
                .flatMap(privateKey -> asymmetricCryptoProvider.sign(unencryptedBytes, privateKey));

        Single.zip(signSingle, signSingle, Arrays::equals)
                .test()
                .assertValue(true);
    }

    @Test
    public void verify_validSignature_completes() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        asymmetricCryptoProvider.generateKeyPair(ALIAS_NEW, context)
                .flatMapCompletable(keyPair -> asymmetricCryptoProvider.sign(unencryptedBytes, keyPair.getPrivate())
                        .flatMapCompletable(signature -> asymmetricCryptoProvider.verify(unencryptedBytes, signature, keyPair.getPublic())))
                .test()
                .assertComplete();
    }

    @Test
    public void verify_invalidSignature_emitsError() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        asymmetricCryptoProvider.generateKeyPair(ALIAS_NEW, context)
                .flatMapCompletable(keyPair -> asymmetricCryptoProvider.verify(unencryptedBytes, unencryptedBytes, keyPair.getPublic()))
                .test()
                .assertError(SignatureException.class);
    }

    @Test
    public void verify_validSignatureFromWrongKey_emitsError() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);
        byte[] signature = asymmetricCryptoProvider.generateKeyPair(ALIAS_NEW, context)
                .flatMap(keyPair -> asymmetricCryptoProvider.sign(unencryptedBytes, keyPair.getPrivate()))
                .blockingGet();

        asymmetricCryptoProvider.getKeyPair(ALIAS_DEFAULT)
                .flatMapCompletable(keyPair -> asymmetricCryptoProvider.verify(unencryptedBytes, signature, keyPair.getPublic()))
                .test()
                .assertError(SignatureException.class);
    }

    @Test
    public void getVerificationResult_validSignature_emitsTrue() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        asymmetricCryptoProvider.generateKeyPair(ALIAS_NEW, context)
                .flatMap(keyPair -> asymmetricCryptoProvider.sign(unencryptedBytes, keyPair.getPrivate())
                        .flatMap(signature -> asymmetricCryptoProvider.getVerificationResult(unencryptedBytes, signature, keyPair.getPublic())))
                .test()
                .assertValue(true);
    }

    @Test
    public void getVerificationResult_invalidSignature_emitsFalse() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        asymmetricCryptoProvider.generateKeyPair(ALIAS_NEW, context)
                .flatMap(newKeyPair -> asymmetricCryptoProvider.sign(unencryptedBytes, newKeyPair.getPrivate())
                        .flatMap(signature -> asymmetricCryptoProvider.getKeyPair(ALIAS_DEFAULT)
                                .flatMap(defaultKeyPair -> asymmetricCryptoProvider.getVerificationResult(unencryptedBytes, signature, defaultKeyPair.getPublic()))))
                .test()
                .assertValue(false);
    }

    @Test
    public void generateKeyPair_validConfiguration_emitsKeyPair() {
        asymmetricCryptoProvider.generateKeyPair(ALIAS_NEW, context)
                .test()
                .assertValue(keyPair -> keyPair.getPublic().getAlgorithm().equals(asymmetricCryptoProvider.getKeyAlgorithm())
                        && keyPair.getPrivate().getAlgorithm().equals(asymmetricCryptoProvider.getKeyAlgorithm()));
    }

    @Test
    public void generateKeyPair_subsequentCalls_emitsDistinctKeyPairs() {
        Single<KeyPair> generateKeyPairSingle = asymmetricCryptoProvider.generateKeyPair(ALIAS_NEW, context);

        Single.zip(generateKeyPairSingle, generateKeyPairSingle, Objects::equals)
                .test()
                .assertValue(false);
    }

}