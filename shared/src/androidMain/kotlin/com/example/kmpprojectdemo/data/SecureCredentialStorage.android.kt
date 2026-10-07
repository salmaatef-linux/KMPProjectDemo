package com.example.kmpprojectdemo.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

actual class SecureCredentialStorage actual constructor() {
    private val prefs by lazy {
        val context = AndroidAppContext.context as? Context
        if (context != null) {
            try {
                val masterKey = MasterKey.Builder(context)
                    .build()
                EncryptedSharedPreferences.create(
                    context,
                    "secure_auth_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e: Exception) {
                context.getSharedPreferences("secure_auth_prefs_fallback", Context.MODE_PRIVATE)
            }
        } else {
            null
        }
    }

    actual fun save(key: String, value: String) {
        prefs?.edit()?.putString(key, value)?.apply()
    }

    actual fun get(key: String): String? {
        return prefs?.getString(key, null)
    }

    actual fun remove(key: String) {
        prefs?.edit()?.remove(key)?.apply()
    }
}
