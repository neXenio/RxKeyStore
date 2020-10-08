package com.nexenio.rxkeystore.provider.cipher.symmetric;

import android.util.Base64;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.RxCryptoProvider;
import com.nexenio.rxkeystore.provider.cipher.BaseCipherProviderTest;
import com.nexenio.rxkeystore.provider.cipher.RxDecryptionException;
import com.nexenio.rxkeystore.provider.cipher.asymmetric.RxAsymmetricCipherProvider;

import org.junit.Ignore;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Ignore("Abstract base class for tests only")
public abstract class BaseSymmetricCryptoProviderTest extends BaseCipherProviderTest {

    protected RxSymmetricCipherProvider symmetricCryptoProvider;

    @Override
    protected RxCryptoProvider createCryptoProvider(@NonNull RxKeyStore keyStore) {
        this.symmetricCryptoProvider = createSymmetricCryptoProvider(keyStore);
        return symmetricCryptoProvider;
    }

    protected abstract RxSymmetricCipherProvider createSymmetricCryptoProvider(@NonNull RxKeyStore keyStore);

    @Override
    protected Completable generateDefaultKeys() {
        return symmetricCryptoProvider.generateKey(ALIAS_DEFAULT, context)
                .flatMapCompletable(secretKey -> symmetricCryptoProvider.setKey(ALIAS_DEFAULT, secretKey));
    }

    @Test
    public void encrypt_validData_emitsEncryptedData() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        symmetricCryptoProvider.generateKey(ALIAS_NEW, context)
                .flatMap(key -> symmetricCryptoProvider.encrypt(unencryptedBytes, key)
                        .flatMap(encryptedBytesAndIV -> symmetricCryptoProvider.decrypt(encryptedBytesAndIV.first, encryptedBytesAndIV.second, key)))
                .test()
                .assertValue(decryptedBytes -> Arrays.equals(unencryptedBytes, decryptedBytes));
    }

    /**
     * Subsequent calls to {@link RxAsymmetricCipherProvider#encrypt(byte[], Key)} should emit
     * distinct data for the same input, even if the same key was used. If the same data is emitted,
     * that indicates a misuse of a static initialization vector or a bad {@link SecureRandom}
     * provider.
     */
    @Test
    public void encrypt_subsequentCallsWithSameKey_emitsDistinctData() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        Single<byte[]> getEncryptedBytesSingle = symmetricCryptoProvider.getKey(ALIAS_DEFAULT)
                .flatMap(key -> symmetricCryptoProvider.encrypt(unencryptedBytes, key))
                .map(pair -> pair.first);

        Single.zip(getEncryptedBytesSingle, getEncryptedBytesSingle, Arrays::equals)
                .test()
                .assertValue(false);
    }

    @Test
    public void decrypt_validData_emitsDecryptedData() {
        byte[] unencryptedBytes = Base64.decode(ENCODED_SYMMETRIC_KEY, Base64.DEFAULT);

        symmetricCryptoProvider.generateKey(ALIAS_NEW, context)
                .flatMap(key -> symmetricCryptoProvider.encrypt(unencryptedBytes, key)
                        .flatMap(encryptedBytesAndIV -> symmetricCryptoProvider.decrypt(encryptedBytesAndIV.first, encryptedBytesAndIV.second, key)))
                .test()
                .assertValue(decryptedBytes -> Arrays.equals(unencryptedBytes, decryptedBytes));
    }

    @Test
    public void decrypt_invalidInitializationVector_emitsError() {
        byte[] unencryptedBytes = LOREM_IPSUM_LONG.getBytes(StandardCharsets.UTF_8);

        symmetricCryptoProvider.generateKey(ALIAS_NEW, context)
                .flatMap(key -> symmetricCryptoProvider.decrypt(unencryptedBytes, null, key))
                .test()
                .assertError(RxDecryptionException.class);
    }

}