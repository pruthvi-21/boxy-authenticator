package com.ps.tokky.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ps.tokky.utils.AppSettings
import com.ps.tokky.utils.Constants.LOGIN_PIN_LENGTH
import com.ps.tokky.utils.CryptoUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor() : ViewModel() {

    private val _passcode = mutableStateOf<List<String>>(emptyList())
    val passcode: State<List<String>> = _passcode

    private var onLoginSuccess: (() -> Unit)? = null
    private var onBiometricPrompt: (() -> Unit)? = null

    fun registerCallbacks(
        onLoginSuccess: () -> Unit,
        onBiometricPrompt: () -> Unit
    ) {
        this.onLoginSuccess = onLoginSuccess
        this.onBiometricPrompt = onBiometricPrompt
    }

    fun appendCharacter(char: String) {
        if (_passcode.value.size < LOGIN_PIN_LENGTH) {
            _passcode.value += char
        }
    }

    fun deleteCharacter() {
        if (_passcode.value.isNotEmpty()) {
            _passcode.value = _passcode.value.dropLast(1)
        }
    }

    fun verifyPin(context: Context, onVerify: (status: Boolean) -> Unit) {
        viewModelScope.launch {
            if (_passcode.value.size < 4) onVerify(false)

            val status = AppSettings.verifyPIN(
                context,
                CryptoUtils.hashPasscode(passcode.value.joinToString(separator = ""))
            )
            if (!status) {
                _passcode.value = emptyList()
            }
            onVerify(status)
        }
    }

    fun loginSuccess() {
        onLoginSuccess?.invoke()
    }

    fun promptForBiometrics() {
        onBiometricPrompt?.invoke()
    }
}
