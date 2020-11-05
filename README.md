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

To make use of it, the library also provides [RxCryptoProvider][rxcryptoprovider] interfaces, further extended in [RxSymmetricCipherProvider][rxsymmetriccipherprovider], [RxAsymmetricCipherProvider][rxasymmetriccipherprovider] and other providers. You can implement them suiting your needs or use default implementations, e.g. [AES][rxaescryptoprovider], [RSA][rxrsacryptoprovider] or [EC][rxeccryptoprovider].

For usage samples, check out the [instrumentation tests][connectedtests].

### Key store

You can use an `RxKeyStore` instance to get or delete entries and to initialize an `RxCryptoProvider`.

#### Get a key store

To work with the default Android keystore, simply use the default constructor:

```java
RxKeyStore androidKeyStore = new RxKeyStore();
```

The actual `KeyStore` from the Android framework will be initialized lazily once its needed. You can also directly access it using `getLoadedKeyStore()`.

You can also use a custom keystore type and crypto provider if you prefer:

```java
RxKeyStore bouncyCastleKeyStore = new RxKeyStore(RxKeyStore.TYPE_BKS, RxKeyStore.PROVIDER_BOUNCY_CASTLE);
```

You may want to use the `load` and `save` methods to restore and persist custom keystores to the filesystem, which expect an `InputStream` or `OutputStream`, respectively.

### Cipher provider

An `RxCipherProvider` is in charge of generating keys and using them for cryptographic operations. All cipher providers either implement [RxSymmetricCipherProvider][rxsymmetriccipherprovider] or [RxAsymmetricCipherProvider][rxasymmetriccipherprovider].

#### Get a cipher provider

You can use default implementations for [AES][rxaescryptoprovider], [RSA][rxrsacryptoprovider] or [EC][rxeccryptoprovider]. You can also create your own by implementing `RxSymmetricCipherProvider` or `RxAsymmetricCipherProvider`.

```java
RxAsymmetricCipherProvider cipherProvider = new RsaCipherProvider(keyStore);
```

#### Generate keys

```java
// symmetric
symmetricCipherProvider.generateKey("my_fancy_key", context)
        .subscribe(secretKey -> {
            // use secret key to encrypt data?
        });

// asymmetric
asymmetricCipherProvider.generateKeyPair("my_fancy_keypair", context)
        .subscribe(keyPair -> {
            PublicKey publicKey = keyPair.getPublic();
            // transfer public key to second party?

            PrivateKey privateKey = keyPair.getPrivate();
            // use private key to sign data?
        });
```

#### Encrypt data

```java
byte[] unencryptedBytes = ...;
Key key = ...;

cipherProvider.encrypt(unencryptedBytes, key)
        .subscribe(encryptedBytesAndIV -> {
            byte[] encryptedBytes = encryptedBytesAndIV.first;
            byte[] initializationVector = encryptedBytesAndIV.second;
            // transfer encrypted data and IV to second party?
        });
```

If you don't want to use a random initialization vector, you can also specify a custom one:

```java
byte[] unencryptedBytes = ...;
byte[] initializationVector = ...;
Key key = ...;

cipherProvider.encrypt(unencryptedBytes, initializationVector, key)
        .subscribe(encryptedBytes -> {
            // transfer encrypted data and IV to second party?
        });
```

#### Decrypt data

```java
byte[] encryptedBytes = ...;
byte[] initializationVector = ...;
Key key = ...;

cipherProvider.decrypt(encryptedBytes, initializationVector, key)
        .subscribe(decryptedBytes -> {
            // process decrypted data?
        });
```

### Signature provider

An `RxSignatureProvider` is in charge of generating and verifying [signatures](https://en.wikipedia.org/wiki/Digital_signature).

#### Get a signature provider

You can get an `RxSignatureProvider` instance by using the base class and specifying the desired signature algorithm. Available signature algorithms supported by the default Android crypto provider are listed [here](https://developer.android.com/reference/java/security/Signature).

```java
RxSignatureProvider signatureProvider = new BaseSignatureProvider(keyStore, "SHA256withECDSA");
```

#### Create a signature

```java
byte[] data = ...;
PrivateKey privateKey = ...;

signatureProvider.sign(data, privateKey)
        .subscribe(signature -> {
            // transfer signature to second party?
        });
```

#### Verify a signature

```java
byte[] data = ...;
byte[] signature = ...;
PublicKey publicKey = ...;

signatureProvider.verify(data, signature, publicKey)
        .subscribe(() -> {
            // signature is valid!
        }, throwable -> {
            // signature is invalid!
        });
```

If you don't want to treat invalid signatures as an error, you can also use `getVerificationResult` instead of `verify`, which will emit a `boolean` that you can check.

### MAC provider

An `RxMacProvider` is in charge of generating and verifying [message authentication codes](https://en.wikipedia.org/wiki/Message_authentication_code), which you can use like signatures if you only have symmetric secret keys instead of asymmetric key pairs.

#### Get a MAC provider

You can get an `RxMacProvider` instance by using the base class and specifying the desired MAC algorithm. Available MAC algorithms supported by the default Android crypto provider are listed [here](https://developer.android.com/reference/javax/crypto/Mac).


```java
RxMacProvider macProvider = new new BaseMacProvider(keyStore, "HmacSHA256");
```

#### Create a MAC

```java
byte[] data = ...;
SecretKey secretKey = ...;

macProvider.sign(data, secretKey)
        .subscribe(mac -> {
            // transfer MAC to second party?
        });
```

#### Verify a MAC

```java
byte[] data = ...;
byte[] mac = ...;
SecretKey secretKey = ...;

macProvider.verify(data, mac, secretKey)
        .subscribe(() -> {
            // MAC is valid
        }, throwable -> {
            // MAC is invalid
        });
```

If you don't want to treat invalid message authentication codes as an error, you can also use `getVerificationResult` instead of `verify`, which will emit a `boolean` that you can check.

[releases]: https://github.com/neXenio/RxKeyStore/releases
[jitpack]: https://jitpack.io/#neXenio/RxKeyStore/
[rxjava]: https://github.com/ReactiveX/RxJava
[androidkeystoretraining]: https://developer.android.com/training/articles/keystore
[keystore]: https://developer.android.com/reference/java/security/KeyStore.html
[rxkeystore]: rxkeystore/src/main/java/com/nexenio/rxkeystore/RxKeyStore.java
[rxcryptoprovider]: rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/RxCryptoProvider.java
[rxsymmetriccipherprovider]: rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/cipher/symmetric/RxSymmetricCipherProvider.java
[rxasymmetriccipherprovider]:rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/cipher/asymmetric/RxAsymmetricCipherProvider.java
[rxaescryptoprovider]: rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/cipher/symmetric/aes/AesCipherProvider.java
[rxrsacryptoprovider]: rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/cipher/asymmetric/rsa/RsaCipherProvider.java
[rxeccryptoprovider]: rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/cipher/asymmetric/ec/EcCipherProvider.java
[connectedtests]: rxkeystore/src/androidTest/java/com/nexenio/rxkeystore
