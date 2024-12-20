package com.ps.tokky.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.ps.tokky.R
import com.ps.tokky.ui.screens.AuthenticationScreen
import com.ps.tokky.ui.theme.TokkyTheme
import com.ps.tokky.utils.AppSettings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity() {

    private val executor by lazy { ContextCompat.getMainExecutor(this) }
    private var biometricPrompt: BiometricPrompt? = null
    private val promptInfo by lazy {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.auth_activity_biometric_unlock_prompt_title))
            .setNegativeButtonText(getString(R.string.auth_activity_biometric_unlock_prompt_negative_text))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false

        setContent {
            TokkyTheme {
                AuthenticationScreen()
            }
        }

        if (!AppSettings.isAppLockEnabled(this) ||
            AppSettings.getPasscodeHash(this) == null
        ) {
            AppSettings.setAppLockEnabled(this, false)
            loginSuccess()
        }

        biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    loginSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Log.e(TAG, "onAuthenticationError: Failed to verify biometrics")
                }

                override fun onAuthenticationFailed() {
                    Log.w(TAG, "onAuthenticationFailed: Incorrect attempt")
                }
            })
    }

    fun bioPrompt() {
        biometricPrompt?.authenticate(promptInfo)
    }

    override fun onResume() {
        super.onResume()

        if (AppSettings.isBiometricAvailable(this) &&
            AppSettings.isBiometricUnlockEnabled(this)
        ) {
            bioPrompt()
        }
    }

    fun loginSuccess() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        private const val TAG = "AuthenticationActivity"
    }
}