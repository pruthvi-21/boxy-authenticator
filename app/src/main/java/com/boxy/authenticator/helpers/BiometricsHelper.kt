package com.boxy.authenticator.helpers

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.boxy.authenticator.R

class BiometricsHelper {

    fun authenticate(context: Context, onSuccess: () -> Unit) {
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt =
            BiometricPrompt(context as FragmentActivity, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Log.e(TAG, "onAuthenticationError: Failed attempt")
                }

                override fun onAuthenticationFailed() {
                    Log.w(TAG, "onAuthenticationFailed: Incorrect attempt")
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setTitle(context.getString(R.string.biometric_prompt_title))
            .setNegativeButtonText(context.getString(R.string.cancel))
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

    companion object {
        private const val TAG = "BiometricsHelper"

        fun areBiometricsAvailable(context: Context): Boolean {
            val biometricManager = context.getSystemService(android.hardware.biometrics.BiometricManager::class.java)
            return (biometricManager.canAuthenticate() == android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS)
        }
    }
}