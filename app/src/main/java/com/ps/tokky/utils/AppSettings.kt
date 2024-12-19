package com.ps.tokky.utils

import android.content.Context
import android.content.SharedPreferences
import android.hardware.biometrics.BiometricManager
import androidx.preference.PreferenceManager
import com.ps.tokky.R

object AppSettings {

    fun setAppLockEnabled(context: Context, isEnabled: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit()
            .putBoolean(context.getString(R.string.key_app_lock), isEnabled)
            .apply()
        if (!isEnabled) preferences.edit().remove(context.getString(R.string.key_app_lock_hash)).apply()
    }

    fun isAppLockEnabled(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(
            context.getString(R.string.key_app_lock),
            context.resources.getBoolean(R.bool.default_app_lock_value)
        )
    }

    fun isBiometricUnlockEnabled(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(
            context.getString(R.string.key_biometric_unlock),
            context.resources.getBoolean(R.bool.default_biometric_unlock_value)
        )
    }

    fun isScreenshotModeEnabled(context: Context): Boolean {
        val prefs = getPreferences(context)
        return prefs.getBoolean(
            context.getString(R.string.key_allow_screenshots),
            context.resources.getBoolean(R.bool.default_allow_screenshots_value)
        )
    }

    fun isBiometricAvailable(context: Context): Boolean {
        val biometricManager = context.getSystemService(BiometricManager::class.java)
        return (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS)
    }

    fun verifyPIN(context: Context, passcodeHash: String): Boolean {

        val storedPasscodeHash = getPreferences(context)
            .getString(context.getString(R.string.key_app_lock_hash), null)
        return storedPasscodeHash == passcodeHash
    }

    fun getPasscodeHash(context: Context): String? {
        return getPreferences(context)
            .getString(context.getString(R.string.key_app_lock_hash), null)
    }

    fun setPasscodeHash(context: Context, passcode: String) {
        getPreferences(context)
            .edit()
            .putString(context.getString(R.string.key_app_lock_hash), passcode)
            .apply()
    }

    fun getAppTheme(context: Context): String {
        val default = context.getString(R.string.default_app_theme_value)
        return getPreferences(context)
            .getString(context.getString(R.string.key_app_theme), default)
            ?: default
    }

    fun getUseBlacksEnabled(context: Context): Boolean {
        return getPreferences(context)
            .getBoolean(
                context.getString(R.string.key_use_black_theme),
                context.resources.getBoolean(R.bool.default_use_black_theme_value)
            )
    }

    fun getPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}