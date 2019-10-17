package com.nexenio.rxkeystore.provider;

import android.content.Context;

import com.nexenio.rxkeystore.RxKeyStore;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Ignore;
import org.junit.Test;

import java.security.Provider;
import java.security.Security;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import io.reactivex.Completable;

@Ignore("Abstract base class for tests only")
public abstract class BaseCryptoProviderTest {

    protected static final String ENCODED_SYMMETRIC_KEY = "1aXXjYd4XmQAp9jt1x2X3t7TmdZT1GoHXg7GLrAE18J/31rxTcb1UYJ9jqx0VoIN0UgvP6tzDk0Ju85GhCMn8lcWHJ9cOAjBUVfAo8nojTFTq/T2YuyaKQtowpF7XQn4d98Am7SLs0LEEmfxOupNCFHJiwekMQP2dgusbxaIpJ0=";

    protected static final String LOREM_IPSUM_LONG = "Cras pharetra pulvinar interdum. Integer bibendum neque dolor, quis sodales dui bibendum non. Curabitur eleifend massa eros, sollicitudin porta lacus convallis eget. Mauris molestie vulputate mi. Pellentesque sit amet tortor a justo tincidunt viverra faucibus a erat. Praesent nec ante eget leo placerat congue eget id elit. Vivamus maximus consectetur malesuada. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Sed at bibendum lorem, ut bibendum nunc. Quisque ut justo non augue egestas rhoncus quis vitae neque. Integer feugiat diam sapien, eu tincidunt ipsum rutrum vitae. Cras pharetra pulvinar interdum. Integer bibendum neque dolor, quis sodales dui bibendum non. Curabitur eleifend massa eros, sollicitudin porta lacus convallis eget. Mauris molestie vulputate mi. Pellentesque sit amet tortor a justo tincidunt viverra faucibus a erat. Praesent nec ante eget leo placerat congue eget id elit. Vivamus maximus consectetur malesuada. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Sed at bibendum lorem, ut bibendum nunc. Quisque ut justo non augue egestas rhoncus quis vitae neque. Integer feugiat diam sapien, eu tincidunt ipsum rutrum vitae. Cras pharetra pulvinar interdum. Integer bibendum neque dolor, quis sodales dui bibendum non. Curabitur eleifend massa eros, sollicitudin porta lacus convallis eget. Mauris molestie vulputate mi. Pellentesque sit amet tortor a justo tincidunt viverra faucibus a erat. Praesent nec ante eget leo placerat congue eget id elit. Vivamus maximus consectetur malesuada. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Sed at bibendum lorem, ut bibendum nunc. Quisque ut justo non augue egestas rhoncus quis vitae neque. Integer feugiat diam sapien, eu tincidunt ipsum rutrum.";

    protected static final String ALIAS_DEFAULT = "default";
    protected static final String ALIAS_NEW = "new";

    protected Context context;
    protected RxKeyStore keyStore;
    protected RxCryptoProvider cryptoProvider;

    @CallSuper
    protected void setUpBeforeEachTest() {
        setupSecurityProviders();

        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        keyStore = createKeyStore();
        cryptoProvider = createCryptoProvider(keyStore);

        resetKeyStore().andThen(generateDefaultKeys())
                .test()
                .assertComplete();
    }

    protected void setupSecurityProviders() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (!(provider instanceof BouncyCastleProvider)) {
            // Android registers its own BC provider. As it might be outdated and might not include
            // all needed ciphers, we substitute it with a known BC bundled in the app.
            // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
            // of that it's possible to have another BC implementation loaded in VM.
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
            Security.insertProviderAt(new BouncyCastleProvider(), 1);
        }
    }

    protected RxKeyStore createKeyStore() {
        return new RxKeyStore(RxKeyStore.TYPE_BKS, RxKeyStore.PROVIDER_BOUNCY_CASTLE);
    }

    protected abstract RxCryptoProvider createCryptoProvider(@NonNull RxKeyStore keyStore);

    private Completable resetKeyStore() {
        return keyStore.deleteAllEntries();
    }

    protected abstract Completable generateDefaultKeys();

    /**
     * Makes sure that {@link #generateDefaultKeys()} actually inserted something with the {@link
     * #ALIAS_DEFAULT} into the {@link #keyStore}.
     */
    @Test
    public void setup_defaultKeyInserted() {
        keyStore.getAliases()
                .test()
                .assertValues(ALIAS_DEFAULT);
    }

}