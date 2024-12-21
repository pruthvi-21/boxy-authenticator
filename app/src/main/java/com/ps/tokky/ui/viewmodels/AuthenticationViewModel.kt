package com.ps.tokky.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ps.tokky.helpers.AppSettings
import com.ps.tokky.helpers.BiometricsHelper
import com.ps.tokky.utils.Constants.LOGIN_PIN_LENGTH
import com.ps.tokky.utils.CryptoUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val biometricsHelper: BiometricsHelper,
    private val settings: AppSettings
) : ViewModel() {

    private val _passcode = mutableStateOf<List<String>>(emptyList())
    val passcode: State<List<String>> = _passcode

    private var onLoginSuccess: (() -> Unit)? = null

    fun registerCallbacks(
        onLoginSuccess: () -> Unit
    ) {
        this.onLoginSuccess = onLoginSuccess
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

    fun verifyPasscode(onFailed: () -> Unit) {
        viewModelScope.launch {
            if (_passcode.value.size < 4) onFailed()

            val passcodeHash = settings.getPasscodeHash()
            val currentHash = CryptoUtils.hashPasscode(passcode.value.joinToString(separator = ""))

            val status = passcodeHash == currentHash
            if (!status) {
                _passcode.value = emptyList()
                onFailed()
                return@launch
            }
            onLoginSuccess?.invoke()
        }
    }

    fun areBiometricsEnabled(context: Context): Boolean {
        return BiometricsHelper.areBiometricsAvailable(context) &&
                settings.isBiometricUnlockEnabled()
    }

    fun promptForBiometricsIfAvailable(context: Context) {
        if (areBiometricsEnabled(context)) {
            biometricsHelper.authenticate(context) {
                onLoginSuccess?.invoke()
            }
        }
    }

    companion object {
        private const val TAG = "AuthenticationViewModel"
    }
}
