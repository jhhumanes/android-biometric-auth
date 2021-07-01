package com.josehumaneshumanes.biometricauth.framework.crypto

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
import androidx.security.crypto.MasterKeys

const val PREFS_TOKEN_KEY = "encryptedToken"

private const val PREFS_FILENAME = "biometric_prefs"

class AndroidCrypto(private val context: Context) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        PREFS_FILENAME,
        masterKeyAlias,
        context,
        AES256_SIV,
        AES256_GCM
    )

    fun putPreference(key: String, value: String) {
        sharedPreferences.edit(commit = true) {
            putString(key, value)
        }
    }

    fun getPreference(key: String): String? = sharedPreferences.getString(key, null)

    fun removePreference(key: String) {
        sharedPreferences.edit(commit = true) {
            remove(key)
        }
    }
}
