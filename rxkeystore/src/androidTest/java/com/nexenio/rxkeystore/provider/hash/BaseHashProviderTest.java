package com.nexenio.rxkeystore.provider.hash;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.BaseCryptoProviderTest;
import com.nexenio.rxkeystore.provider.RxCryptoProvider;

import org.junit.Test;

import java.util.Arrays;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Single;

public abstract class BaseHashProviderTest extends BaseCryptoProviderTest {

    protected RxHashProvider hashProvider;

    @Override
    protected RxCryptoProvider createCryptoProvider(@NonNull RxKeyStore keyStore) {
        this.hashProvider = createHashProvider(keyStore);
        return hashProvider;
    }

    protected abstract RxHashProvider createHashProvider(@NonNull RxKeyStore keyStore);

    @Test
    public void hash_sameInput_sameOutput() {
        Single<byte[]> hash = hashProvider.hash(LOREM_IPSUM_LONG.getBytes());

        Single.zip(hash, hash, Arrays::equals)
                .test()
                .assertValue(true);
    }

    @Test
    public void hash_distinctInput_distinctOutput() {
        Single<byte[]> hash1 = hashProvider.hash((LOREM_IPSUM_LONG + "!").getBytes());
        Single<byte[]> hash2 = hashProvider.hash((LOREM_IPSUM_LONG + "?").getBytes());

        Single.zip(hash1, hash2, Arrays::equals)
                .test()
                .assertValue(false);
    }

    @Test
    public void hash_shortAndLongInput_equalLengthOutput() {
        Single<Integer> hashLength1 = hashProvider.hash(LOREM_IPSUM_LONG.substring(0, 100).getBytes())
                .map(bytes -> bytes.length);
        Single<Integer> hashLength2 = hashProvider.hash(LOREM_IPSUM_LONG.substring(0, 10).getBytes())
                .map(bytes -> bytes.length);

        Single.zip(hashLength1, hashLength2, Integer::equals)
                .test()
                .assertValue(true);
    }

}