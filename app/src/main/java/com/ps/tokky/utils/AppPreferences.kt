package com.ps.tokky.utils

import android.content.Context
import android.hardware.biometrics.BiometricManager
import androidx.preference.PreferenceManager

class AppPreferences private constructor(context: Context) {

    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val biometricManager = context.getSystemService(BiometricManager::class.java)

    var appLockEnabled: Boolean = false
        get() {
            return sharedPreferences.getBoolean(KEY_APP_LOCK, false)
        }
        set(value) {
            field = value
            sharedPreferences.edit().putBoolean(KEY_APP_LOCK, value).apply()
            if (!value) sharedPreferences.edit().remove(KEY_PASSCODE_HASH).apply()
        }

    var biometricUnlockEnabled: Boolean = false
        get() {
            return sharedPreferences.getBoolean(KEY_BIOMETRIC_UNLOCK, false)
        }
        set(value) {
            field = value
            sharedPreferences.edit().putBoolean(KEY_BIOMETRIC_UNLOCK, value).apply()
        }

    var allowScreenshots: Boolean = false
        get() {
            return sharedPreferences.getBoolean(KEY_ALLOW_SCREENSHOTS, false)
        }
        set(value) {
            field = value
            sharedPreferences.edit().putBoolean(KEY_ALLOW_SCREENSHOTS, value).apply()
        }

    var displayIcon = true
        get() {
            return sharedPreferences.getBoolean(KEY_SHOW_THUMBNAILS, true)
        }
        set(value) {
            field = value
            sharedPreferences.edit().putBoolean(KEY_SHOW_THUMBNAILS, value).apply()
        }

    fun isBiometricAvailable(): Boolean {
        return (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS)
    }

    fun verifyPIN(passcodeHash: String): Boolean {
        val storedPasscodeHash = sharedPreferences.getString(KEY_PASSCODE_HASH, null)
        return storedPasscodeHash == passcodeHash
    }

    fun setPIN(passcode: String) {
        sharedPreferences.edit().putString(KEY_PASSCODE_HASH, passcode).apply()
    }

    companion object {
        private var instance: AppPreferences? = null

        const val KEY_PASSCODE_HASH = "key_passcode_hash"

        const val KEY_APP_LOCK = "key_app_lock"
        const val KEY_BIOMETRIC_UNLOCK = "key_biometric_unlock"

        const val KEY_ALLOW_SCREENSHOTS = "key_allow_screenshots"

        const val KEY_SHOW_THUMBNAILS = "key_show_thumbnail"
        const val KEY_IMPORT_ACCOUNTS = "key_import_accounts"
        const val KEY_EXPORT_ACCOUNTS = "key_export_accounts"

        fun getInstance(context: Context): AppPreferences {
            if (instance == null) {
                instance = AppPreferences(context)
            }
            return instance!!
        }
    }
}