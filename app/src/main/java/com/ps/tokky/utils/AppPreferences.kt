package com.ps.tokky.utils

import android.content.Context
import androidx.preference.PreferenceManager

class AppPreferences private constructor(context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var appLockEnabled: Boolean = false
        get() {
            return sharedPreferences.getBoolean(KEY_APP_LOCK, false)
        }
        set(value) {
            field = value
            sharedPreferences.edit().putBoolean(KEY_APP_LOCK, value).apply()
        }

    companion object {
        private var instance: AppPreferences? = null
        private const val KEY_APP_LOCK = "key_app_lock"

        fun getInstance(context: Context): AppPreferences {
            if (instance == null) {
                instance = AppPreferences(context)
            }
            return instance!!
        }
    }
}