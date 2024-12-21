package com.ps.tokky.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ps.tokky.helpers.AppSettings
import com.ps.tokky.ui.screens.AuthenticationScreen
import com.ps.tokky.ui.theme.TokkyTheme
import com.ps.tokky.ui.viewmodels.AuthenticationViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity() {

    @Inject
    lateinit var settings: AppSettings

    private val authViewModel: AuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false

        setContent {
            TokkyTheme {
                AuthenticationScreen(authViewModel)
            }
        }

        authViewModel.registerCallbacks(
            onLoginSuccess = {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        )

        if (!settings.isAppLockEnabled() ||
            settings.getPasscodeHash() == null
        ) {
            settings.setAppLockEnabled(false)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        authViewModel.promptForBiometricsIfAvailable(this)
    }

    companion object {
        private const val TAG = "AuthenticationActivity"
    }
}