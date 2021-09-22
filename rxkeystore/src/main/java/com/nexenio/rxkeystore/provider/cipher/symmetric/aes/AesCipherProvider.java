package com.nexenio.rxkeystore.provider.cipher.symmetric.aes;

import android.content.Context;
import android.os.Build;

import com.nexenio.rxkeystore.RxKeyStore;
import com.nexenio.rxkeystore.provider.cipher.symmetric.BaseSymmetricCipherProvider;

import javax.crypto.SecretKey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import io.reactivex.rxjava3.core.Single;

import static com.nexenio.rxkeystore.RxKeyStore.BLOCK_MODE_GCM;
import static com.nexenio.rxkeystore.RxKeyStore.ENCRYPTION_PADDING_NONE;
import static com.nexenio.rxkeystore.RxKeyStore.KEY_ALGORITHM_AES;

@RequiresApi(api = Build.VERSION_CODES.M)
public class AesCipherProvider extends BaseSymmetricCipherProvider {

    private static final int KEY_SIZE = 256;
    private static final String[] BLOCK_MODES = new String[]{BLOCK_MODE_GCM};
    private static final String[] ENCRYPTION_PADDINGS = new String[]{ENCRYPTION_PADDING_NONE};

    private static final String TRANSFORMATION_ALGORITHM = "AES/GCM/NoPadding";

    public AesCipherProvider(RxKeyStore rxKeyStore) {
        super(rxKeyStore, KEY_ALGORITHM_AES);
    }

    @Override
    public Single<SecretKey> generateKey(@NonNull String alias, @NonNull Context context) {
        return getKeyGeneratorInstance()
                .map(keyGenerator -> {
                    SecretKey secretKey;
                    synchronized (keyGenerator) {
                        keyGenerator.init(KEY_SIZE);
                        secretKey = keyGenerator.generateKey();
                    }
                    return secretKey;
                });
    }

    @Override
    public Single<SecretKey> generateKey(@NonNull String alias, @Nullable Integer keyPurposes, @NonNull Context context) {
        return generateKey(alias, context);
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
