# SecureSharedPreference

![GitHub issues](https://img.shields.io/github/issues/AbhiTheNullPointer/SecureSharedPreference)
![GitHub forks](https://img.shields.io/github/forks/AbhiTheNullPointer/SecureSharedPreference)
![GitHub stars](https://img.shields.io/github/stars/AbhiTheNullPointer/SecureSharedPreference)
[![](https://jitpack.io/v/AbhiTheNullPointer/SecureSharedPreference.svg)](https://jitpack.io/#AbhiTheNullPointer/SecureSharedPreference)
![License](https://img.shields.io/github/license/AbhiTheNullPointer/SecureSharedPreference)
[![CI](https://github.com/AbhiTheNullPointer/SecureSharedPreference/actions/workflows/ci.yml/badge.svg)](https://github.com/AbhiTheNullPointer/SecureSharedPreference/actions/workflows/ci.yml)

An Android library that provides **encrypted SharedPreferences** with hardware-backed security using Android KeyStore. Drop-in replacement for standard SharedPreferences with automatic AES-256-GCM encryption.

> **📦 Try it now** — download the latest demo APK from the [Releases](https://github.com/AbhiTheNullPointer/SecureSharedPreference/releases/latest) page and sideload it to test the library on a real device without writing any code.

## Why SecureSharedPreference?

- 🔐 **Hardware-backed encryption** — Keys live in Android KeyStore, never exposed to the app
- ⚡ **Fast** — Key cached after first load; no repeated KeyStore round-trips
- 🚀 **Zero dependencies** — Pure Android SDK, no third-party libraries
- 📱 **Wide compatibility** — Android 6.0+ (API 23+), tested on emulators and real devices
- 🔧 **Simple API** — Identical shape to standard `SharedPreferences`
- ✅ **No deprecated APIs** — Modern, future-proof code
- 🛡 **Tamper-proof** — AES-GCM authentication tag detects any modification

## Installation

### Step 1 — Add JitPack to your root `build.gradle` (or `settings.gradle`)

**Legacy `build.gradle` style:**
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

**Modern `settings.gradle` style (Gradle 7+):**
```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2 — Add the dependency

```gradle
dependencies {
    implementation 'com.github.AbhiTheNullPointer:SecureSharedPreference:1.0.0'
}
```

## Usage

```java
import io.github.AbhiTheNullPointer.secureprefs.SecureSharedPreference;

// One-line setup
SecureSharedPreference prefs = new SecureSharedPreference(context, "my_prefs");

// Store (automatically encrypted)
prefs.putString("username", "john_doe");
prefs.putInt("user_id", 12345);
prefs.putLong("timestamp", System.currentTimeMillis());
prefs.putFloat("score", 98.6f);
prefs.putBoolean("is_premium", true);

// Retrieve (automatically decrypted)
String  username  = prefs.getString("username", "guest");
int     userId    = prefs.getInt("user_id", 0);
long    ts        = prefs.getLong("timestamp", 0L);
float   score     = prefs.getFloat("score", 0f);
boolean isPremium = prefs.getBoolean("is_premium", false);

// Utility
boolean exists = prefs.contains("username");
prefs.remove("username");   // remove one key
prefs.clear();              // remove all keys (KeyStore key is preserved)
```

See [EXAMPLE.md](EXAMPLE.md) for more examples.

## How It Works

1. **Key generation** — On first use an AES-256 key is generated inside Android KeyStore (hardware-backed on most modern devices). It never leaves the secure enclave.
2. **Key lookup** — Key names are stored as `Base64(SHA-256(name))` so the same name always maps to the same slot, without exposing what the key is.
3. **Value encryption** — Each value is encrypted with AES-GCM. A fresh 12-byte random IV is generated per write and prepended to the ciphertext, so the same value stored twice produces different ciphertext.
4. **Retrieval** — On read, the IV is extracted, the value decrypted, and the GCM authentication tag verified — any tampering throws a `BadPaddingException`, which is caught and causes the default value to be returned.

## Supported Types

| Method | Type |
|--------|------|
| `putString` / `getString` | `String` |
| `putInt` / `getInt` | `int` |
| `putLong` / `getLong` | `long` |
| `putFloat` / `getFloat` | `float` |
| `putBoolean` / `getBoolean` | `boolean` |

## Comparison

| Feature | SecureSharedPreference | androidx.security.crypto | Standard SharedPreferences |
|---------|------------------------|--------------------------|----------------------------|
| Encryption | ✅ AES-256-GCM | ✅ AES-256-GCM | ❌ None |
| Hardware-backed | ✅ Yes | ✅ Yes | ❌ No |
| Zero dependencies | ✅ Yes | ❌ No | ✅ Yes |
| Deprecated APIs | ✅ None | ⚠️ Some | ✅ None |
| Setup lines | ✅ 1 | ⚠️ ~5 | ✅ 1 |

## Requirements

- **Min SDK**: 23 (Android 6.0 Marshmallow)
- **Compile SDK**: 34
- **Permissions**: None
- **Dependencies**: None

## Contributing

Pull requests are welcome. Please open an issue first to discuss major changes.

## Author

Made with ❤️ by [AbhiTheNullPointer](https://github.com/AbhiTheNullPointer)

## License

[MIT](LICENSE)
