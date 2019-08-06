[![Travis](https://img.shields.io/travis/neXenio/RxKeyStore/master.svg)](https://travis-ci.org/neXenio/RxKeyStore/builds) [![GitHub release](https://img.shields.io/github/release/neXenio/RxKeyStore.svg)](https://github.com/neXenio/RxKeyStore/releases) [![JitPack](https://img.shields.io/jitpack/v/neXenio/RxKeyStore.svg)](https://jitpack.io/#neXenio/RxKeyStore/) [![Codecov](https://img.shields.io/codecov/c/github/nexenio/RxKeyStore.svg)](https://codecov.io/gh/neXenio/RxKeyStore) [![license](https://img.shields.io/github/license/neXenio/RxKeyStore.svg)](https://github.com/neXenio/RxKeyStore/blob/master/LICENSE)

# RxKeyStore

This library provides an [RxJava][rxjava] wrapper for the [Android Keystore][androidkeystoretraining], as well as utilities to use it for cryptographic operations.

## Features

- CRUD for keys and certificates
- Symmetric cryptography (AES)
    - Generate secret keys
    - Encrypt & Decrypt
- Asymmetric cryptography (RSA | EC)
    - Generate key pairs
    - Encrypt & Decrypt
    - Sign & Verify

## Usage

### Integration

You can get the latest artifacts from [JitPack][jitpack]:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.neXenio:RxKeyStore:dev-SNAPSHOT'
}
```

### Overview

The entrypoint of this library is [RxKeyStore][rxkeystore]. It provides the functionality of the [Android keystore][keystore] in a reactive fashion.

To make use of it, the library also provides the [RxCryptoProvider][rxcryptoprovider] interface, further extended in [RxSymmetricCryptoProvider][rxsymmetriccryptoprovider] and [RxAsymmetricCryptoProvider][rxasymmetriccryptoprovider]. You can implement them suiting your needs or use default implementations using [AES][rxaescryptoprovider], [RSA][rxrsacryptoprovider] or [EC][rxeccryptoprovider].

For usage samples, check out the [instrumentation tests][connectedtests].

[releases]: https://github.com/neXenio/RxKeyStore/releases
[jitpack]: https://jitpack.io/#neXenio/RxKeyStore/
[rxjava]: https://github.com/ReactiveX/RxJava
[androidkeystoretraining]: https://developer.android.com/training/articles/keystore
[keystore]: https://developer.android.com/reference/java/security/KeyStore.html
[rxkeystore]: https://github.com/neXenio/RxKeyStore/blob/master/rxkeystore/src/main/java/com/nexenio/rxkeystore/RxKeyStore.java
[rxcryptoprovider]: https://github.com/neXenio/RxKeyStore/blob/master/rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/RxCryptoProvider.java
[rxsymmetriccryptoprovider]: https://github.com/neXenio/RxKeyStore/blob/master/rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/symmetric/RxSymmetricCryptoProvider.java
[rxasymmetriccryptoprovider]: https://github.com/neXenio/RxKeyStore/blob/master/rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/asymmetric/RxAsymmetricCryptoProvider.java
[rxaescryptoprovider]: https://github.com/neXenio/RxKeyStore/blob/master/rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/symmetric/aes/RxAESCryptoProvider.java
[rxrsacryptoprovider]: https://github.com/neXenio/RxKeyStore/blob/master/rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/asymmetric/rsa/RxRSACryptoProvider.java
[rxeccryptoprovider]: https://github.com/neXenio/RxKeyStore/tree/master/rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/asymmetric
[connectedtests]: https://github.com/neXenio/RxKeyStore/tree/master/rxkeystore/src/androidTest/java/com/nexenio/rxkeystore
