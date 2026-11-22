package io.github.AbhiTheNullPointer.securesharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

/**
 * Encrypted key-value storage for Android applications.
 *
 * <p>String and primitive values are encrypted with AES-GCM before they are stored in a private
 * {@link SharedPreferences} file. Preference names are represented by deterministic SHA-256
 * hashes so they can be looked up consistently. Hashing obscures names but does not protect
 * predictable names from guessing.
 *
 * <p>This class provides convenience methods for common value types. It does not implement the
 * full {@link SharedPreferences} API.
 *
 * <h3>Usage</h3>
 * <pre>
 * SecureSharedPreference prefs = new SecureSharedPreference(context, "my_prefs");
 *
 * prefs.setData("username", "john_doe");
 * prefs.setData("age", 25);
 * prefs.setData("isPremium", true);
 *
 * String username = prefs.getData("username", "guest");
 * int age = prefs.getData("age", 0);
 * boolean premium = prefs.getData("isPremium", false);
 * </pre>
 *
 * @author AbhiTheNullPointer (Abhi)
 * @version 1.0
 * @since API 23
 */
public class SecureSharedPreference {

    private final SharedPreferences preferences;
    private final GuardSquareEncryption encryption;
    private final GuardSquare guardSquare;
    private SecretKey cachedKey;

    /**
     * Creates (or opens) a secure preferences file.
     *
     * @param context application or activity context
     * @param preferenceName name of the underlying preferences file
     * @throws RuntimeException if the Android Keystore key cannot be initialized
     */
    public SecureSharedPreference(Context context, String preferenceName) {
        this.preferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        this.encryption = new GuardSquareEncryption();
        this.guardSquare = new GuardSquare();
        initializeKey();
    }

    /**
     * Stores {@code value} encrypted under {@code key}.
     *
     * @param key preference key
     * @param value plaintext string to encrypt
     * @throws RuntimeException if encryption or storage fails
     */
    public void putString(String key, String value) {
        try {
            preferences.edit().putString(hashKey(key), encryption.encrypt(value, cachedKey)).apply();
        } catch (Exception e) {
            throw new RuntimeException("Failed to store string for key: " + key, e);
        }
    }

    /**
     * Returns the decrypted string, or {@code defaultValue} if the key is missing or unreadable.
     *
     * @param key preference key
     * @param defaultValue value returned when the stored value is unavailable
     */
    public String getString(String key, String defaultValue) {
        try {
            String encrypted = preferences.getString(hashKey(key), null);
            if (encrypted == null) return defaultValue;
            return encryption.decrypt(encrypted, cachedKey);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Stores {@code value} encrypted under {@code key}.
     *
     * @param key preference key
     * @param value integer to encrypt
     * @throws RuntimeException if encryption or storage fails
     */
    public void putInt(String key, int value) {
        try {
            preferences.edit().putString(hashKey(key), encryption.encrypt(String.valueOf(value), cachedKey)).apply();
        } catch (Exception e) {
            throw new RuntimeException("Failed to store int for key: " + key, e);
        }
    }

    /**
     * Returns the decrypted integer, or {@code defaultValue} if the key is missing or unreadable.
     *
     * @param key preference key
     * @param defaultValue value returned when the stored value is unavailable
     */
    public int getInt(String key, int defaultValue) {
        try {
            String encrypted = preferences.getString(hashKey(key), null);
            if (encrypted == null) return defaultValue;
            return Integer.parseInt(encryption.decrypt(encrypted, cachedKey));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Stores {@code value} encrypted under {@code key}.
     *
     * @param key preference key
     * @param value long integer to encrypt
     * @throws RuntimeException if encryption or storage fails
     */
    public void putLong(String key, long value) {
        try {
            preferences.edit().putString(hashKey(key), encryption.encrypt(String.valueOf(value), cachedKey)).apply();
        } catch (Exception e) {
            throw new RuntimeException("Failed to store long for key: " + key, e);
        }
    }

    /**
     * Returns the decrypted long, or {@code defaultValue} if the key is missing or unreadable.
     *
     * @param key preference key
     * @param defaultValue value returned when the stored value is unavailable
     */
    public long getLong(String key, long defaultValue) {
        try {
            String encrypted = preferences.getString(hashKey(key), null);
            if (encrypted == null) return defaultValue;
            return Long.parseLong(encryption.decrypt(encrypted, cachedKey));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Stores {@code value} encrypted under {@code key}.
     *
     * @param key preference key
     * @param value floating-point value to encrypt
     * @throws RuntimeException if encryption or storage fails
     */
    public void putFloat(String key, float value) {
        try {
            preferences.edit().putString(hashKey(key), encryption.encrypt(String.valueOf(value), cachedKey)).apply();
        } catch (Exception e) {
            throw new RuntimeException("Failed to store float for key: " + key, e);
        }
    }

    /**
     * Returns the decrypted float, or {@code defaultValue} if the key is missing or unreadable.
     *
     * @param key preference key
     * @param defaultValue value returned when the stored value is unavailable
     */
    public float getFloat(String key, float defaultValue) {
        try {
            String encrypted = preferences.getString(hashKey(key), null);
            if (encrypted == null) return defaultValue;
            return Float.parseFloat(encryption.decrypt(encrypted, cachedKey));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Stores {@code value} encrypted under {@code key}.
     *
     * @param key preference key
     * @param value boolean value to encrypt
     * @throws RuntimeException if encryption or storage fails
     */
    public void putBoolean(String key, boolean value) {
        try {
            preferences.edit().putString(hashKey(key), encryption.encrypt(String.valueOf(value), cachedKey)).apply();
        } catch (Exception e) {
            throw new RuntimeException("Failed to store boolean for key: " + key, e);
        }
    }

    /**
     * Returns the decrypted boolean, or {@code defaultValue} if the key is missing or unreadable.
     *
     * @param key preference key
     * @param defaultValue value returned when the stored value is unavailable
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            String encrypted = preferences.getString(hashKey(key), null);
            if (encrypted == null) return defaultValue;
            return Boolean.parseBoolean(encryption.decrypt(encrypted, cachedKey));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /** Stores an encrypted string value.
     * @param key preference key
     * @param value string value to store
     */
    public void setData(String key, String value) { putString(key, value); }

    /** Stores an encrypted integer value.
     * @param key preference key
     * @param value integer value to store
     */
    public void setData(String key, int value) { putInt(key, value); }

    /** Stores an encrypted long value.
     * @param key preference key
     * @param value long value to store
     */
    public void setData(String key, long value) { putLong(key, value); }

    /** Stores an encrypted floating-point value.
     * @param key preference key
     * @param value floating-point value to store
     */
    public void setData(String key, float value) { putFloat(key, value); }

    /** Stores an encrypted boolean value.
     * @param key preference key
     * @param value boolean value to store
     */
    public void setData(String key, boolean value) { putBoolean(key, value); }

    /** Returns a decrypted string or {@code defaultValue} when it is unavailable.
     * @param key preference key
     * @param defaultValue value returned when the stored value is unavailable
     */
    public String getData(String key, String defaultValue) { return getString(key, defaultValue); }

    /** Returns a decrypted integer or {@code defaultValue} when it is unavailable.
     * @param key preference key
     * @param defaultValue value returned when the stored value is unavailable
     */
    public int getData(String key, int defaultValue) { return getInt(key, defaultValue); }

    /** Returns a decrypted long integer or {@code defaultValue} when it is unavailable.
     * @param key preference key
     * @param defaultValue value returned when the stored value is unavailable
     */
    public long getData(String key, long defaultValue) { return getLong(key, defaultValue); }

    /** Returns a decrypted floating-point value or {@code defaultValue} when it is unavailable.
     * @param key preference key
     * @param defaultValue value returned when the stored value is unavailable
     */
    public float getData(String key, float defaultValue) { return getFloat(key, defaultValue); }

    /** Returns a decrypted boolean or {@code defaultValue} when it is unavailable.
     * @param key preference key
     * @param defaultValue value returned when the stored value is unavailable
     */
    public boolean getData(String key, boolean defaultValue) { return getBoolean(key, defaultValue); }

    /** Removes the value stored under {@code key}.
     * @param key preference key
     */
    public void removeData(String key) { remove(key); }

    /**
     * Removes the value stored under {@code key}.
     *
     * @param key preference key
     * @throws RuntimeException if the preference update fails
     */
    public void remove(String key) {
        try {
            preferences.edit().remove(hashKey(key)).apply();
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove key: " + key, e);
        }
    }

    /** Clears all stored values. The KeyStore key is not affected. */
    public void clear() {
        preferences.edit().clear().apply();
    }

    /**
     * Returns {@code true} if a value is stored under {@code key}.
     *
     * @param key preference key
     */
    public boolean contains(String key) {
        try {
            return preferences.contains(hashKey(key));
        } catch (Exception e) {
            return false;
        }
    }

    private void initializeKey() {
        try {
            guardSquare.generateKey();
            cachedKey = guardSquare.getKey();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize the encryption key. The device must support Android Keystore (API 23+).", e);
        }
    }

    /**
     * Produces a deterministic, URL-safe Base64(SHA-256) hash of the key name.
     * Using a hash (not encryption) is essential: AES-GCM uses a random IV so
     * encrypting the same key twice produces different ciphertexts, making lookup
     * impossible without iterating every entry.
     */
    private String hashKey(String key) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
            // NO_WRAP + NO_PADDING keeps the string free of newlines and '=' padding
            return Base64.encodeToString(hash, Base64.NO_WRAP | Base64.NO_PADDING | Base64.URL_SAFE);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed on every Android device
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}

