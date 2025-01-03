package com.ps.tokky.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ps.tokky.App
import com.ps.tokky.helpers.AppSettings
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val appSettings: AppSettings by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false

        setContent {
            App()
        }
    }

    override fun onResume() {
        super.onResume()

        setScreenshotMode(appSettings.isScreenshotModeEnabled())
    }

    fun setScreenshotMode(isEnabled: Boolean) {
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