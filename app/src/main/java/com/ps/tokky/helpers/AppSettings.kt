package com.ps.tokky.helpers

import com.ps.tokky.data.preferences.PreferenceStore
import com.ps.tokky.utils.AppTheme

class AppSettings(
    private val store: PreferenceStore
) {

    fun setAppTheme(theme: AppTheme) {
        store.putString(Keys.APP_THEME, theme.name)
    }

    fun getAppTheme(): AppTheme {
        val themeName = store.getString(Keys.APP_THEME)
        return try {
            AppTheme.valueOf(themeName ?: AppTheme.SYSTEM.name)
        } catch (e: IllegalArgumentException) {
            AppTheme.SYSTEM
        }
    }

    fun setAppLockEnabled(isEnabled: Boolean) {
        store.putBoolean(Keys.APP_LOCK, isEnabled)
        if (!isEnabled) store.remove(Keys.APP_LOCK_HASH)
    }

    fun isAppLockEnabled(default: Boolean = Defaults.APP_LOCK): Boolean {
        return try {
            store.getBoolean(Keys.APP_LOCK, default)
        } catch (e: Exception) {
            default
        }
    }

    fun setBiometricUnlockEnabled(isEnabled: Boolean) {
        store.putBoolean(Keys.BIOMETRIC_UNLOCK, isEnabled)
    }

    fun isBiometricUnlockEnabled(default: Boolean = Defaults.BIOMETRIC_UNLOCK): Boolean {
        return try {
            store.getBoolean(Keys.BIOMETRIC_UNLOCK, default)
        } catch (e: Exception) {
            default
        }
    }

    fun setScreenshotModeEnabled(allow: Boolean) {
        store.putBoolean(Keys.SCREENSHOT_MODE, allow)
    }

    fun isScreenshotModeEnabled(default: Boolean = Defaults.SCREENSHOT_MODE): Boolean {
        return try {
            store.getBoolean(Keys.SCREENSHOT_MODE, default)
        } catch (e: Exception) {
            default
        }
    }

    fun setPasscodeHash(passcodeHash: String) {
        store.putString(Keys.APP_LOCK_HASH, passcodeHash)
    }

    fun getPasscodeHash(): String? {
        return store.getString(Keys.APP_LOCK_HASH, null)
    }

    companion object {
        object Keys {
            const val APP_THEME = "key_app_theme"
            const val APP_LOCK = "key_app_lock"
            const val APP_LOCK_HASH = "key_app_lock_hash"
            const val BIOMETRIC_UNLOCK = "key_biometric_unlock"
            const val SCREENSHOT_MODE = "key_screenshots_mode"
        }

        object Defaults {
            const val APP_LOCK = false
            const val BIOMETRIC_UNLOCK = false
            const val SCREENSHOT_MODE = false
        }
    }
}