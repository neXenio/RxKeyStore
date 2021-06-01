package com.nexenio.rxkeystore.provider.asymmetric.rsa;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.BaseCryptoProviderTest;
import com.nexenio.rxkeystore.provider.RxCryptoProvider;
import com.nexenio.rxkeystore.provider.asymmetric.RxAsymmetricCryptoProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import io.reactivex.Completable;

public class RxRSACryptoProvider2Test extends BaseCryptoProviderTest {

    protected RxAsymmetricCryptoProvider asymmetricCryptoProvider;

    @Before
    @Override
    public void setUpBeforeEachTest() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        keyStore = createKeyStore();
        cryptoProvider = createCryptoProvider(keyStore);
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

    @Override
    protected Completable generateDefaultKeys() {
        return null;
    }

    protected RxAsymmetricCryptoProvider createAsymmetricCryptoProvider(@NonNull RxKeyStore keyStore) {
        return new RxRSACryptoProvider(keyStore);
    }

    @Test
    public void generateKeyPair_arabicAsLocale_emitsKeyPair() {
        Locale actualLocale = Locale.getDefault();
        Locale expectedLocale = new Locale("ar");
        Locale.setDefault(expectedLocale);

        try {
            asymmetricCryptoProvider.generateKeyPair("alias", context)
                    .test()
                    .assertNoErrors();
            Assert.assertEquals(Locale.getDefault(), expectedLocale);
        } finally {
            // clean up
            Locale.setDefault(actualLocale);
        }
    }

    @Ignore("Only test bug fix")
    @Test
    @Override
    public void setup_defaultKeyInserted() {

    }

}
