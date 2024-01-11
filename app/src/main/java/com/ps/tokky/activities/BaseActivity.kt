package com.ps.tokky.activities

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ps.tokky.R
import com.ps.tokky.database.DBHelper
import com.ps.tokky.utils.AppSettings

open class BaseActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    protected val db by lazy { DBHelper.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (AppSettings.getAppTheme(this)) {
            getString(R.string.app_theme_light_value) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            getString(R.string.app_theme_dark_value) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            getString(R.string.app_theme_follow_system_value) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                if (AppSettings.getUseBlacksEnabled(this)) setTheme(R.style.Theme_Tokky_Black)
            }
        }

        AppSettings
            .getPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppSettings
            .getPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            getString(R.string.key_allow_screenshots) -> setScreenshotMode(AppSettings.isScreenshotModeEnabled(this))
            getString(R.string.key_use_monospace_font) -> if (this is MainActivity) recreate()
        }
    }

    override fun onResume() {
        super.onResume()

        setScreenshotMode(AppSettings.isScreenshotModeEnabled(this))
    }

    private fun setScreenshotMode(isEnabled: Boolean) {
        if (isEnabled) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}