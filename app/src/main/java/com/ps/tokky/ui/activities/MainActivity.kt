package com.ps.tokky.ui.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ps.tokky.App
import com.ps.tokky.helpers.AppSettings
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var appSettings: AppSettings

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