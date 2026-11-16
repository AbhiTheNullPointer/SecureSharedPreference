# Changelog

## 1.0.0 — 2026-03-06

### Added
- AES-256-GCM authenticated encryption via Android KeyStore
- Hardware-backed key storage; key material never leaves the secure enclave
- Support for `String`, `int`, `long`, `float`, and `boolean` types
- Zero external dependencies
- Compatible with Android API 23+

### Fixed (in this commit)
- **Critical**: Key names now stored as `SHA-256` hashes instead of AES-GCM ciphertext.
  AES-GCM uses a random IV so the same key name would produce a different ciphertext
  on every call, making it impossible to ever look up a previously stored value.
- Removed `setUnlockedDeviceRequired(true)` which caused `KeyPermanentlyInvalidatedException`
  on lock-screen and background access scenarios.
- Removed `buildscript` block from module `build.gradle` (conflicted with root classpath).
- Aligned Gradle wrapper (7.5) with AGP 7.4.2 (previously used 8.0 which is incompatible).
- Added `getKey()` null-check to prevent NPE when key entry is missing.
- Added `long` and `float` type support.
- Added demo `:app` module so a testable APK can be generated.
- Added `.github/workflows/ci.yml` and `release.yml` for automatic builds and APK uploads.
