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
[rxkeystore]: https://github.com/neXenio/RxKeyStore/
[rxcryptoprovider]: https://github.com/neXenio/RxKeyStore/
[rxsymmetriccryptoprovider]: https://github.com/neXenio/RxKeyStore/
[rxasymmetriccryptoprovider]: https://github.com/neXenio/RxKeyStore/
[rxaescryptoprovider]: https://github.com/neXenio/RxKeyStore/
[rxrsacryptoprovider]: https://github.com/neXenio/RxKeyStore/
[rxeccryptoprovider]: https://github.com/neXenio/RxKeyStore/
[connectedtests]: https://github.com/neXenio/RxKeyStore/
