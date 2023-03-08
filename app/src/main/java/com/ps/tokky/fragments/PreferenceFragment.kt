package com.ps.tokky.fragments

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import com.ps.tokky.R
import com.ps.tokky.preferences.AppLockPreference

class PreferenceFragment : PreferenceFragmentCompat() {

    private val appLockPreference: AppLockPreference? by lazy { findPreference(KEY_APP_LOCK) }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)


        appLockPreference?.setOnPreferenceClickListener {
            val appLockPreference = it as AppLockPreference
            if (appLockPreference.isChecked) {
                biometricPrompt.authenticate(promptInfo)
            } else appLockPreference.click()
            true
        }
    }

    private val executor by lazy { ContextCompat.getMainExecutor(requireContext()) }
    private val promptInfo by lazy {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.disable_app_lock_prompt))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()
    }

    private val biometricPrompt by lazy {
        BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                appLockPreference?.click()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Toast.makeText(context, "Verification failed", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                Log.w(TAG, "onAuthenticationFailed: Incorrect attempt")
            }
        })
    }

    companion object {
        private const val TAG = "PreferenceFragment"
        private const val KEY_APP_LOCK = "key_app_lock"
    }
}