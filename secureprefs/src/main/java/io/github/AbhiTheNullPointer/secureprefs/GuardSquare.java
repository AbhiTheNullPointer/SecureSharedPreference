package io.github.AbhiTheNullPointer.secureprefs;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * GuardSquare - Secure key management using Android KeyStore System.
 *
 * Generates and retrieves an AES-256-GCM key stored inside the hardware-backed
 * Android KeyStore. The raw key material never leaves the secure enclave.
 *
 * Compatible with Android API 23+ (Marshmallow and above).
 */
public class GuardSquare {

    private static final String ALIAS           = "NULLPOINTER_BHAIYA_SECURE_PREFS_V1";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";

    /**
     * Generates an AES-256-GCM key in AndroidKeyStore if one does not already exist.
     * Safe to call on every app start — it is a no-op when the key is already present.
     */
    public void generateKey() throws KeyStoreException, CertificateException, IOException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {

        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);

        if (keyStore.containsAlias(ALIAS)) {
            return; // Key already exists
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);

        keyGenerator.init(
                new KeyGenParameterSpec.Builder(
                        ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setKeySize(256)
                        // Randomised encryption ensures a fresh IV is required each time.
                        .setRandomizedEncryptionRequired(true)
                        .build());

        keyGenerator.generateKey();
    }

    /**
     * Returns the AES-256 SecretKey from AndroidKeyStore.
     *
     * @throws IllegalStateException if the key is not found (call {@link #generateKey()} first).
     */
    public SecretKey getKey() throws KeyStoreException, CertificateException, IOException,
            NoSuchAlgorithmException, UnrecoverableEntryException {

        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);

        KeyStore.SecretKeyEntry entry =
                (KeyStore.SecretKeyEntry) keyStore.getEntry(ALIAS, null);

        if (entry == null) {
            throw new IllegalStateException(
                    "Encryption key not found in KeyStore. Call generateKey() first.");
        }

        return entry.getSecretKey();
    }
}
