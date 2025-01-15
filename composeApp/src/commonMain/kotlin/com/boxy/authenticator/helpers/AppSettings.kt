package com.boxy.authenticator.helpers

import com.boxy.authenticator.data.preferences.PreferenceStore
import com.boxy.authenticator.utils.AppTheme

class AppSettings(
    private val store: PreferenceStore,
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

    fun setAppLockEnabled(isEnabled: Boolean, passwordHash: String = "") {
        store.putBoolean(Keys.APP_LOCK, isEnabled)
        if (!isEnabled) {
            store.remove(Keys.APP_LOCK_HASH)
        } else {
            if (passwordHash.isEmpty()) throw IllegalArgumentException("Password hash is empty")
            store.putString(Keys.APP_LOCK_HASH, passwordHash)
        }
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

    fun setBlockScreenshotsEnabled(block: Boolean) {
        store.putBoolean(Keys.BLOCK_SCREENSHOTS, block)
    }

    fun isBlockScreenshotsEnabled(default: Boolean = Defaults.BLOCK_SCREENSHOTS): Boolean {
        return try {
            store.getBoolean(Keys.BLOCK_SCREENSHOTS, default)
        } catch (e: Exception) {
            default
        }
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
            const val BLOCK_SCREENSHOTS = "key_block_screenshots"
        }

        object Defaults {
            const val APP_LOCK = false
            const val BIOMETRIC_UNLOCK = false
            const val BLOCK_SCREENSHOTS = true
        }
    }
}