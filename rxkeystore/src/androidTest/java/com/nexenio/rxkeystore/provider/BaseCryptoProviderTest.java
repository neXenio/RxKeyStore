package com.nexenio.rxkeystore.provider;

import android.content.Context;

import com.nexenio.rxkeystore.RxKeyStore;

import org.junit.Ignore;
import org.junit.Test;

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

    protected void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        keyStore = new RxKeyStore();
        cryptoProvider = createCryptoProvider(keyStore);

        resetKeyStore().andThen(generateDefaultKeys())
                .test()
                .assertComplete();
    }

    protected abstract RxCryptoProvider createCryptoProvider(@NonNull RxKeyStore keyStore);

    private Completable resetKeyStore() {
        return keyStore.deleteAllEntries();
    }

    protected abstract Completable generateDefaultKeys();

    @Test
    public void getCipherInstance() {

    }

    @Test
    public void getKeyAlgorithm() {

    }

}