package io.github.AbhiTheNullPointer.secureprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

/**
 * SecureSharedPreference - Encrypted SharedPreferences Library
 *
 * <p>A drop-in replacement for Android SharedPreferences with automatic AES-256-GCM encryption.
 * All values are encrypted before storage. Keys are stored as SHA-256 hashes so that the same
 * key always maps to the same lookup string (a requirement that rules out randomised/IV-based
 * encryption for key names).
 *
 * <h3>Usage</h3>
 * <pre>
 * SecureSharedPreference prefs = new SecureSharedPreference(context, "my_prefs");
 *
 * prefs.putString("username", "john_doe");
 * prefs.putInt("age", 25);
 * prefs.putBoolean("isPremium", true);
 *
 * String username  = prefs.getString("username", "guest");
 * int    age       = prefs.getInt("age", 0);
 * boolean premium  = prefs.getBoolean("isPremium", false);
 * </pre>
 *
 * @author NullPointerBhaiya (Abhi)
 * @version 1.0
 * @since API 23
 */
public class SecureSharedPreference {

    private final SharedPreferences    preferences;
    private final GuardSquareEncryption encryption;
    private final GuardSquare          guardSquare;
    private SecretKey                  cachedKey;

    /**
     * Creates (or opens) a secure preferences file.
     *
     * @param context         Application or Activity context
     * @param preferenceName  Name of the underlying SharedPreferences file
     * @throws RuntimeException if the KeyStore key cannot be initialised
     */
    public SecureSharedPreference(Context context, String preferenceName) {
        this.preferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        this.encryption  = new GuardSquareEncryption();
        this.guardSquare = new GuardSquare();
        initializeKey();
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /** Encrypts and stores a String value. */
    public void putString(String key, String value) {
        try {
            preferences.edit()
                    .putString(hashKey(key), encryption.encrypt(value, cachedKey))
                    .apply();
        } catch (Exception e) {
            throw new RuntimeException("Failed to store string for key: " + key, e);
        }
    }

    /** Returns the decrypted String, or {@code defaultValue} if not found. */
    public String getString(String key, String defaultValue) {
        try {
            String encrypted = preferences.getString(hashKey(key), null);
            if (encrypted == null) return defaultValue;
            return encryption.decrypt(encrypted, cachedKey);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /** Encrypts and stores an int value. */
    public void putInt(String key, int value) {
        try {
            preferences.edit()
                    .putString(hashKey(key), encryption.encrypt(String.valueOf(value), cachedKey))
                    .apply();
        } catch (Exception e) {
            throw new RuntimeException("Failed to store int for key: " + key, e);
        }
    }

    /** Returns the decrypted int, or {@code defaultValue} if not found. */
    public int getInt(String key, int defaultValue) {
        try {
            String encrypted = preferences.getString(hashKey(key), null);
            if (encrypted == null) return defaultValue;
            return Integer.parseInt(encryption.decrypt(encrypted, cachedKey));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /** Encrypts and stores a long value. */
    public void putLong(String key, long value) {
        try {
            preferences.edit()
                    .putString(hashKey(key), encryption.encrypt(String.valueOf(value), cachedKey))
                    .apply();
        } catch (Exception e) {
            throw new RuntimeException("Failed to store long for key: " + key, e);
        }
    }

    /** Returns the decrypted long, or {@code defaultValue} if not found. */
    public long getLong(String key, long defaultValue) {
        try {
            String encrypted = preferences.getString(hashKey(key), null);
            if (encrypted == null) return defaultValue;
            return Long.parseLong(encryption.decrypt(encrypted, cachedKey));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /** Encrypts and stores a float value. */
    public void putFloat(String key, float value) {
        try {
            preferences.edit()
                    .putString(hashKey(key), encryption.encrypt(String.valueOf(value), cachedKey))
                    .apply();
        } catch (Exception e) {
            throw new RuntimeException("Failed to store float for key: " + key, e);
        }
    }

    /** Returns the decrypted float, or {@code defaultValue} if not found. */
    public float getFloat(String key, float defaultValue) {
        try {
            String encrypted = preferences.getString(hashKey(key), null);
            if (encrypted == null) return defaultValue;
            return Float.parseFloat(encryption.decrypt(encrypted, cachedKey));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /** Encrypts and stores a boolean value. */
    public void putBoolean(String key, boolean value) {
        try {
            preferences.edit()
                    .putString(hashKey(key), encryption.encrypt(String.valueOf(value), cachedKey))
                    .apply();
        } catch (Exception e) {
            throw new RuntimeException("Failed to store boolean for key: " + key, e);
        }
    }

    /** Returns the decrypted boolean, or {@code defaultValue} if not found. */
    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            String encrypted = preferences.getString(hashKey(key), null);
            if (encrypted == null) return defaultValue;
            return Boolean.parseBoolean(encryption.decrypt(encrypted, cachedKey));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /** Removes the value stored under {@code key}. */
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

    /** Returns {@code true} if a value is stored under {@code key}. */
    public boolean contains(String key) {
        try {
            return preferences.contains(hashKey(key));
        } catch (Exception e) {
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private void initializeKey() {
        try {
            guardSquare.generateKey();
            cachedKey = guardSquare.getKey();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to initialise encryption key. " +
                    "Device must support Android KeyStore (API 23+).", e);
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
