package com.ps.tokky.utils

import android.content.Context
import androidx.preference.PreferenceManager

class AppPreferences private constructor(context: Context) {

    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var appLockEnabled: Boolean = false
        get() {
            return sharedPreferences.getBoolean(KEY_APP_LOCK, false)
        }
        set(value) {
            field = value
            sharedPreferences.edit().putBoolean(KEY_APP_LOCK, value).apply()
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

    companion object {
        private var instance: AppPreferences? = null

        const val KEY_APP_LOCK = "key_app_lock"
        const val KEY_ALLOW_SCREENSHOTS = "key_allow_screenshots"

        const val KEY_SHOW_THUMBNAILS = "key_show_thumbnail"

        fun getInstance(context: Context): AppPreferences {
            if (instance == null) {
                instance = AppPreferences(context)
            }
            return instance!!
        }
    }
}