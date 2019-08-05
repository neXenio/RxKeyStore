package com.nexenio.rxkeystore.provider.symmetric.aes;

import android.os.Build;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.symmetric.BaseSymmetricCryptoProvider;

import androidx.annotation.RequiresApi;

import static com.nexenio.rxkeystore.RxKeyStore.BLOCK_MODE_CBC;
import static com.nexenio.rxkeystore.RxKeyStore.ENCRYPTION_PADDING_PKCS7;
import static com.nexenio.rxkeystore.RxKeyStore.KEY_ALGORITHM_AES;

@RequiresApi(api = Build.VERSION_CODES.M)
public class RxAESCryptoProvider extends BaseSymmetricCryptoProvider {

    private static final String[] BLOCK_MODES = new String[]{BLOCK_MODE_CBC};
    private static final String[] ENCRYPTION_PADDINGS = new String[]{ENCRYPTION_PADDING_PKCS7};

    private static final String TRANSFORMATION_ALGORITHM = "AES/CBC/PKCS7Padding";

    public RxAESCryptoProvider(RxKeyStore rxKeyStore) {
        super(rxKeyStore, KEY_ALGORITHM_AES);
    }

    @Override
    protected String[] getBlockModes() {
        return BLOCK_MODES;
    }

    @Override
    protected String[] getEncryptionPaddings() {
        return ENCRYPTION_PADDINGS;
    }

    @Override
    protected String getTransformationAlgorithm() {
        return TRANSFORMATION_ALGORITHM;
    }

}
