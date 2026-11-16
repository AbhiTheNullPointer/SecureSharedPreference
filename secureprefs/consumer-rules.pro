# Consumer ProGuard rules for apps using SecureSharedPreference

# Keep all public API
-keep public class io.github.AbhiTheNullPointer.secureprefs.SecureSharedPreference {
    public *;
}

# Keep encryption internals
-keep class io.github.AbhiTheNullPointer.secureprefs.** { *; }
