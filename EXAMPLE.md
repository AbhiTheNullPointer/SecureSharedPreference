# Usage examples

## Basic usage

```java
import android.content.Context;
import io.github.AbhiTheNullPointer.securesharedpreference.SecureSharedPreference;

public final class UserSettings {
    private final SecureSharedPreference preferences;

    public UserSettings(Context context) {
        preferences = new SecureSharedPreference(
                context.getApplicationContext(), "user_settings");
    }

    public void save(String username, int userId, boolean premium) {
        preferences.putString("username", username);
        preferences.putInt("user_id", userId);
        preferences.putBoolean("premium", premium);
    }

    public String username() {
        return preferences.getString("username", "guest");
    }

    public int userId() {
        return preferences.getInt("user_id", 0);
    }

    public boolean isPremium() {
        return preferences.getBoolean("premium", false);
    }
}
```

## Session values

```java
public final class SessionStore {
    private final SecureSharedPreference preferences;

    public SessionStore(Context context) {
        preferences = new SecureSharedPreference(context, "session");
    }

    public void save(String accountId, String token) {
        preferences.putString("account_id", accountId);
        preferences.putString("access_token", token);
    }

    public String accessToken() {
        return preferences.getString("access_token", "");
    }

    public void clear() {
        preferences.clear();
    }
}
```

`clear()` removes all entries in this preferences file but keeps the Keystore key. If a value cannot be decrypted, getters return the default value supplied by the caller. Keep any fallback or recovery behavior specific to your application.

## Migrating existing values

Migration must account for the value types used by the old preferences file. Copy and verify each value before removing anything from the old file. For example:

```java
import android.content.Context;
import android.content.SharedPreferences;
import io.github.AbhiTheNullPointer.securesharedpreference.SecureSharedPreference;

SharedPreferences oldPreferences = context.getSharedPreferences(
        "old_settings", Context.MODE_PRIVATE);
SecureSharedPreference securePreferences = new SecureSharedPreference(
        context, "secure_settings");

if (oldPreferences.contains("username")) {
    securePreferences.putString("username", oldPreferences.getString("username", ""));
}
if (oldPreferences.contains("user_id")) {
    securePreferences.putInt("user_id", oldPreferences.getInt("user_id", 0));
}
```

Do not delete the old preferences until your app has verified the migrated data and has a recovery plan. This library does not provide a bulk import or transactional migration API.

## Handling initialization errors

Creating the object initializes an Android Keystore key and can fail on unsupported or misconfigured devices. Catch the exception at your app's boundary and decide how the app should recover:

```java
try {
    SecureSharedPreference preferences =
            new SecureSharedPreference(context, "private_values");
} catch (RuntimeException exception) {
    // Report the failure and use an application-specific recovery path.
}
```
