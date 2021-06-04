package com.nexenio.rxkeystore.provider.cipher.asymmetric.rsa;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.BaseCryptoProviderTest;
import com.nexenio.rxkeystore.provider.RxCryptoProvider;
import com.nexenio.rxkeystore.provider.cipher.asymmetric.RxAsymmetricCipherProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

public class RsaCipherProvider2Test extends BaseCryptoProviderTest {

    protected RxAsymmetricCipherProvider asymmetricCryptoProvider;

    @Before
    @Override
    @CallSuper
    public void setUpBeforeEachTest() {
        super.setUpBeforeEachTest();
    }

    @Override
    protected RxKeyStore createKeyStore() {
        return new RxKeyStore(RxKeyStore.TYPE_ANDROID, RxKeyStore.PROVIDER_ANDROID_KEY_STORE);
    }

    @Override
    protected RxCryptoProvider createCryptoProvider(@NonNull RxKeyStore keyStore) {
        this.asymmetricCryptoProvider = createAsymmetricCryptoProvider(keyStore);
        return asymmetricCryptoProvider;
    }

    protected RxAsymmetricCipherProvider createAsymmetricCryptoProvider(@NonNull RxKeyStore keyStore) {
        return new RsaCipherProvider(keyStore);
    }

    @Test
    public void generateKeyPair_arabicAsLocale_emitsKeyPair() {
        Locale originalLocale = Locale.getDefault();
        Locale expectedLocale = new Locale("ar");
        Locale.setDefault(expectedLocale);

        try {
            asymmetricCryptoProvider.generateKeyPair("alias", context)
                    .test()
                    .assertNoErrors();
            Assert.assertEquals(Locale.getDefault(), expectedLocale);
        } finally {
            // clean up
            Locale.setDefault(originalLocale);
        }
    }

}
