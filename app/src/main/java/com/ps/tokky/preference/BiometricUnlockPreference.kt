package com.ps.tokky.preference

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.SwitchPreference
import com.ps.tokky.R

class BiometricUnlockPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : SwitchPreference(context, attrs) {

    private val executor by lazy { ContextCompat.getMainExecutor(context) }
    private var biometricPrompt: BiometricPrompt? = null

    override fun onClick() {
        biometricPrompt?.authenticate(promptInfo)
    }

    fun initBiometricPrompt(activity: Fragment) {
        biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super@BiometricUnlockPreference.onClick()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Log.e(TAG, "onAuthenticationError: Failed attempt")
            }

            override fun onAuthenticationFailed() {
                Log.w(TAG, "onAuthenticationFailed: Incorrect attempt")
            }
        })
    }

    private val promptInfo: BiometricPrompt.PromptInfo
        get() {
            val builder = BiometricPrompt.PromptInfo.Builder()
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .setNegativeButtonText(context.getString(R.string.app_lock_prompt_negative_text))

            builder.setTitle(
                context.getString(
                    if (isChecked) R.string.disable_app_lock_prompt else R.string.enable_app_lock_prompt
                )
            )

            return builder.build()
        }

    companion object {
        private const val TAG = "BiometricUnlockPreference"
    }
}