package com.ps.tokky.activities

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ps.tokky.R
import com.ps.tokky.database.DBHelper
import com.ps.tokky.utils.AppSettings

open class BaseActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    protected val db by lazy { DBHelper.getInstance(this) }
    private var currentlyAppliedTheme = ThemeMode.DARK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentlyAppliedTheme = currentThemeMode()
        val themeRes = when (currentThemeMode()) {
            ThemeMode.LIGHT -> R.style.Theme_Tokky_Light
            ThemeMode.DARK -> R.style.Theme_Tokky_Dark
            ThemeMode.BLACK -> R.style.Theme_Tokky_Black
        }
        setTheme(themeRes)

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
            getString(R.string.key_app_theme) -> recreate()
        }
    }

    override fun onResume() {
        super.onResume()

        setScreenshotMode(AppSettings.isScreenshotModeEnabled(this))

        val newTheme = currentThemeMode()
        if (newTheme != currentlyAppliedTheme) {
            Log.d(this.javaClass.name, "Theme change detected, restarting activity")
            recreate()
        }
    }

    private fun setScreenshotMode(isEnabled: Boolean) {
        if (isEnabled) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    private enum class ThemeMode {
        LIGHT, DARK, BLACK;
    }

    private fun currentThemeMode(): ThemeMode {
        return when (AppSettings.getAppTheme(this)) {
            getString(R.string.app_theme_light_value) -> ThemeMode.LIGHT
            getString(R.string.app_theme_dark_value) -> {
                if (AppSettings.getUseBlacksEnabled(this)) ThemeMode.BLACK
                else ThemeMode.DARK
            }

            else -> {
                when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        if (AppSettings.getUseBlacksEnabled(this)) ThemeMode.BLACK
                        else ThemeMode.DARK
                    }

                    else -> ThemeMode.LIGHT
                }
            }
        }
    }
}