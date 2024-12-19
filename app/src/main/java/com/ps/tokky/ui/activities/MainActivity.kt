package com.ps.tokky.ui.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ps.tokky.App
import com.ps.tokky.R
import com.ps.tokky.utils.AppSettings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false

        setContent {
            App()
        }

        AppSettings
            .getPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onResume() {
        super.onResume()

        setScreenshotMode(AppSettings.isScreenshotModeEnabled(this))
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
        }
    }

    private fun setScreenshotMode(isEnabled: Boolean) {
        if (isEnabled) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}