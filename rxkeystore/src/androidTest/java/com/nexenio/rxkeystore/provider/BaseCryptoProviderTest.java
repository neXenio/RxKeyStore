package com.nexenio.rxkeystore.provider;

import android.content.Context;

import com.nexenio.rxkeystore.RxKeyStore;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Ignore;

import java.security.Provider;
import java.security.Security;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;

@Ignore("Abstract base class for tests only")
public abstract class BaseCryptoProviderTest {

    protected static final String LOREM_IPSUM_LONG = "Cras pharetra pulvinar interdum. Integer bibendum neque dolor, quis sodales dui bibendum non. Curabitur eleifend massa eros, sollicitudin porta lacus convallis eget. Mauris molestie vulputate mi. Pellentesque sit amet tortor a justo tincidunt viverra faucibus a erat. Praesent nec ante eget leo placerat congue eget id elit. Vivamus maximus consectetur malesuada. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Sed at bibendum lorem, ut bibendum nunc. Quisque ut justo non augue egestas rhoncus quis vitae neque. Integer feugiat diam sapien, eu tincidunt ipsum rutrum vitae. Cras pharetra pulvinar interdum. Integer bibendum neque dolor, quis sodales dui bibendum non. Curabitur eleifend massa eros, sollicitudin porta lacus convallis eget. Mauris molestie vulputate mi. Pellentesque sit amet tortor a justo tincidunt viverra faucibus a erat. Praesent nec ante eget leo placerat congue eget id elit. Vivamus maximus consectetur malesuada. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Sed at bibendum lorem, ut bibendum nunc. Quisque ut justo non augue egestas rhoncus quis vitae neque. Integer feugiat diam sapien, eu tincidunt ipsum rutrum vitae. Cras pharetra pulvinar interdum. Integer bibendum neque dolor, quis sodales dui bibendum non. Curabitur eleifend massa eros, sollicitudin porta lacus convallis eget. Mauris molestie vulputate mi. Pellentesque sit amet tortor a justo tincidunt viverra faucibus a erat. Praesent nec ante eget leo placerat congue eget id elit. Vivamus maximus consectetur malesuada. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Sed at bibendum lorem, ut bibendum nunc. Quisque ut justo non augue egestas rhoncus quis vitae neque. Integer feugiat diam sapien, eu tincidunt ipsum rutrum.";

    protected static Provider originalProvider;
    protected static int originalPosition;

    protected Context context;
    protected RxKeyStore keyStore;
    protected RxCryptoProvider cryptoProvider;

    @CallSuper
    protected void setUpBeforeEachTest() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        keyStore = createKeyStore();
        cryptoProvider = createCryptoProvider(keyStore);
    }

    public static void setupSecurityProviders() {
        Provider[] providers = Security.getProviders();
        for (int i = 0; i < providers.length; i++) {
            Provider provider = providers[i];
            if (BouncyCastleProvider.PROVIDER_NAME.equals(provider.getName())) {
                originalProvider = provider;
                originalPosition = i;
            }
        }
        if (!(originalProvider instanceof BouncyCastleProvider)) {
            // Android registers its own BC provider. As it might be outdated and might not include
            // all needed ciphers, we substitute it with a known BC bundled in the app.
            // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
            // of that it's possible to have another BC implementation loaded in VM.
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
            Security.insertProviderAt(new BouncyCastleProvider(), originalPosition + 1);
        }
    }

    public static void cleanUpSecurityProviders() {
        if (originalProvider != null) {
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
            Security.insertProviderAt(originalProvider, originalPosition + 1);
        }
    }

    protected RxKeyStore createKeyStore() {
        return new RxKeyStore(RxKeyStore.TYPE_BKS, RxKeyStore.PROVIDER_BOUNCY_CASTLE);
    }

    protected abstract RxCryptoProvider createCryptoProvider(@NonNull RxKeyStore keyStore);

}