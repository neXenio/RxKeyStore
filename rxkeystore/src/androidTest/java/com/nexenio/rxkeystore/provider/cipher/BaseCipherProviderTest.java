package com.nexenio.rxkeystore.provider.cipher;

import com.nexenio.rxkeystore.provider.BaseCryptoProviderTest;

import org.junit.Ignore;
import org.junit.Test;

import androidx.annotation.CallSuper;
import io.reactivex.rxjava3.core.Completable;

@Ignore("Abstract base class for tests only")
public abstract class BaseCipherProviderTest extends BaseCryptoProviderTest {

    protected static final String ENCODED_SYMMETRIC_KEY = "1aXXjYd4XmQAp9jt1x2X3t7TmdZT1GoHXg7GLrAE18J/31rxTcb1UYJ9jqx0VoIN0UgvP6tzDk0Ju85GhCMn8lcWHJ9cOAjBUVfAo8nojTFTq/T2YuyaKQtowpF7XQn4d98Am7SLs0LEEmfxOupNCFHJiwekMQP2dgusbxaIpJ0=";

    protected static final String ALIAS_DEFAULT = "default";
    protected static final String ALIAS_NEW = "new";

    @CallSuper
    protected void setUpBeforeEachTest() {
        super.setUpBeforeEachTest();

        resetKeyStore().andThen(generateDefaultKeys())
                .test()
                .assertComplete();
    }

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