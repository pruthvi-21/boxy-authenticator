package com.ps.tokky.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.ps.tokky.R
import com.ps.tokky.databinding.ActivityAuthenticationBinding
import com.ps.tokky.utils.CryptoUtils

class AuthenticationActivity : BaseActivity(), View.OnClickListener {

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

        val keypadButtons = arrayOf(
            binding.keypad.button1, binding.keypad.button2, binding.keypad.button3,
            binding.keypad.button4, binding.keypad.button5, binding.keypad.button6,
            binding.keypad.button7, binding.keypad.button8, binding.keypad.button9,
            binding.keypad.button0
        )

        for (btn in keypadButtons) {
            btn.addOnClickListener(this)
        }

        binding.keypad.buttonBackspace.addOnClickListener(this)

        binding.btnBiometrics.setOnClickListener {
            biometricPrompt?.authenticate(promptInfo)
        }
    }

    override fun onResume() {
        super.onResume()

        if (preferences.isBiometricEnabled()) {
            biometricPrompt?.authenticate(promptInfo)
            binding.btnBiometrics.visibility = View.VISIBLE
        } else {
            binding.btnBiometrics.visibility = View.GONE
        }
    }

    private fun loginSuccess() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onClick(v: View?) {
        if (v?.id == binding.keypad.buttonBackspace.id) {
            if (passcode.size <= 0) return
            passcode.removeAt(passcode.size - 1)
        } else {
            if (passcode.size >= 4 || v == null || v !is TextView?) return

            passcode.add(v.text.toString())
        }
        verifyPIN()
        updateUI()
    }

    private fun verifyPIN() {
        if (passcode.size < 4) return

        val status = preferences.verifyPIN(CryptoUtils.hashPasscode(passcode.joinToString(separator = "")))
        if (!status) Toast.makeText(this, "Incorrect attempt", Toast.LENGTH_SHORT).show()
        else loginSuccess()
    }

    private fun updateUI() {
        binding.pinField.currentLength = passcode.size
    }

    companion object {
        private const val TAG = "AuthenticationActivity"
    }
}