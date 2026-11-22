# Changelog

## Unreleased

- Fix AES-GCM encryption by letting Android Keystore generate the IV required by randomized encryption keys.
- Improve demo error handling and replace the demo screen with a clearer interface.
- Update Android Gradle Plugin, Gradle wrapper, and compile SDK versions.
- Move the Java API to `io.github.AbhiTheNullPointer.securesharedpreference`.
- Keep the minimum supported Android version at API 23; the library is compatible with newer SDK versions.
- Set Java source and target compatibility to Java 17.
- Add overloaded `setData` and `getData` methods and a `removeData` alias.
- Expand the demo checks to cover CRUD operations, defaults, and all supported value types.
- Clarify the library's security properties and limitations in the documentation.

## 1.0.0 - 2026-03-06

- Add AES-GCM encryption using Android Keystore.
- Add support for `String`, `int`, `long`, `float`, and `boolean` values.
- Hash preference names with SHA-256 for deterministic lookup.
- Include an interactive demo app.
