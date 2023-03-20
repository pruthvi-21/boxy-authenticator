package com.ps.tokky.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.ps.tokky.R
import com.ps.tokky.databinding.ActivityAuthenticationBinding
import com.ps.tokky.utils.AppPreferences.Companion.KEY_PASSCODE_HASH
import com.ps.tokky.utils.CryptoUtils
import com.ps.tokky.views.KeypadLayout
import kotlinx.coroutines.*

class AuthenticationActivity : BaseActivity(), KeypadLayout.OnKeypadKeyClickListener {

    private val binding by lazy { ActivityAuthenticationBinding.inflate(layoutInflater) }

    private val executor by lazy { ContextCompat.getMainExecutor(this) }
    private var biometricPrompt: BiometricPrompt? = null
    private val promptInfo by lazy {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.auth_activity_biometric_unlock_prompt_title))
            .setNegativeButtonText(getString(R.string.auth_activity_biometric_unlock_prompt_negative_text))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()
    }

    private val passcode = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (!preferences.appLockEnabled ||
            preferences.sharedPreferences.getString(KEY_PASSCODE_HASH, null) == null
        ) {
            preferences.appLockEnabled = false
            loginSuccess()
        }

        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
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

        binding.btnBiometrics.setOnClickListener {
            biometricPrompt?.authenticate(promptInfo)
        }

        binding.keypad.keypadKeyClickListener = this
    }

    override fun onResume() {
        super.onResume()

        if (preferences.isBiometricAvailable() && preferences.biometricUnlockEnabled) {
            biometricPrompt?.authenticate(promptInfo)
            binding.btnBiometrics.visibility = View.VISIBLE
        } else {
            binding.btnBiometrics.visibility = View.GONE
        }
    }

    override fun onDigitClick(digit: String) {
        if (passcode.size >= 4) return
        passcode.add(digit)
        updateUI()
        if (passcode.size == 4)
            CoroutineScope(Dispatchers.Default).launch {
                delay(300)
                verifyPIN()
            }
    }

    override fun onBackspaceClick() {
        if (passcode.size <= 0) return
        passcode.removeAt(passcode.size - 1)
        updateUI()
    }

    private fun loginSuccess() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private suspend fun verifyPIN() {
        if (passcode.size < 4) return

        val status = preferences.verifyPIN(CryptoUtils.hashPasscode(passcode.joinToString(separator = "")))
        if (!status) {
            passcode.clear()
            updateUI()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AuthenticationActivity, R.string.settings_app_lock_incorrect_pin, Toast.LENGTH_SHORT)
                    .show()
            }
        } else loginSuccess()
    }

    private fun updateUI() {
        binding.pinField.currentLength = passcode.size
    }

    companion object {
        private const val TAG = "AuthenticationActivity"
    }
}