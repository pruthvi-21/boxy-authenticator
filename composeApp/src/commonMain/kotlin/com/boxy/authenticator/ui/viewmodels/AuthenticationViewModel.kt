package com.boxy.authenticator.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.biometric_prompt_title
import boxy_authenticator.composeapp.generated.resources.cancel
import boxy_authenticator.composeapp.generated.resources.incorrect_password
import boxy_authenticator.composeapp.generated.resources.to_unlock
import com.boxy.authenticator.helpers.AppSettings
import com.boxy.authenticator.utils.HashUtils
import dev.icerock.moko.biometry.BiometryAuthenticator
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class AuthenticationViewModel(
    private val settings: AppSettings,
    private val biometryAuthenticator: BiometryAuthenticator,
) : ViewModel() {

    private val _password = mutableStateOf("")
    val password: State<String> get() = _password

    private val _passwordError = mutableStateOf<String?>(null)
    val passwordError: State<String?> get() = _passwordError

    fun verifyPassword(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val status = HashUtils.verifyHash(password.value, settings.getPasscodeHash() ?: "")
            if (!status) {
                _password.value = ""
                _passwordError.value = getString(Res.string.incorrect_password)
            }
            onComplete(status)
        }
    }

    fun isBiometricUnlockEnabled(): Boolean {
        return biometryAuthenticator.isBiometricAvailable() && settings.isBiometricUnlockEnabled()
    }

    fun promptForBiometrics(
        onComplete: (Boolean) -> Unit,
    ) = viewModelScope.launch {
        try {
            val isSuccess = biometryAuthenticator.checkBiometryAuthentication(
                requestTitle = getString(Res.string.biometric_prompt_title).desc(),
                requestReason = getString(Res.string.to_unlock).desc(),
                failureButtonText = getString(Res.string.cancel).desc(),
                allowDeviceCredentials = false,
            )
            onComplete(isSuccess)
        } catch (throwable: Throwable) {
            println(throwable)
            onComplete(false)
        }
    }

    fun updatePassword(password: String) {
        _passwordError.value = null
        _password.value = password
    }

    companion object {
        private const val TAG = "AuthenticationViewModel"
    }
}