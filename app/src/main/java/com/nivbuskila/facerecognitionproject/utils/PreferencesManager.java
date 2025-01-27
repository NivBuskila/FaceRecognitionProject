package com.nivbuskila.facerecognitionproject.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class PreferencesManager {
    private static final String PREFERENCES_FILE = "secure_prefs";
    private static final String KEY_USERNAME = "admin_username";
    private static final String KEY_PASSWORD = "admin_password";

    private final SharedPreferences preferences;

    public PreferencesManager(Context context) {
        SharedPreferences prefs;
        try {
            // Create master key for encryption
            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                    "_androidx_security_master_key_",
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build();

            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyGenParameterSpec(spec)
                    .build();

            // Create encrypted shared preferences
            prefs = EncryptedSharedPreferences.create(
                    context,
                    PREFERENCES_FILE,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            // Fallback to regular shared preferences if encryption fails
            prefs = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        }
        preferences = prefs;
    }

    public void saveCredentials(String username, String password) {
        preferences.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_PASSWORD, password)
                .apply();
    }

    public String getUsername() {
        return preferences.getString(KEY_USERNAME, "");
    }

    public String getPassword() {
        return preferences.getString(KEY_PASSWORD, "");
    }
}