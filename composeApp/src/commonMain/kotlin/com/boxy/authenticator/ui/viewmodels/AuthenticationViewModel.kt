package com.boxy.authenticator.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.biometric_prompt_title
import boxy_authenticator.composeapp.generated.resources.cancel
import boxy_authenticator.composeapp.generated.resources.incorrect_password
import boxy_authenticator.composeapp.generated.resources.to_unlock
import com.boxy.authenticator.core.AppSettings
import com.boxy.authenticator.core.Logger
import com.boxy.authenticator.core.crypto.HashKeyGenerator
import dev.icerock.moko.biometry.BiometryAuthenticator
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class AuthenticationViewModel(
    private val settings: AppSettings,
    private val biometryAuthenticator: BiometryAuthenticator,
) : ViewModel() {
    private val logger = Logger("AuthenticationViewModel")

    private val _password = mutableStateOf("")
    val password by _password

    private val _passwordError = mutableStateOf<String?>(null)
    val passwordError by _passwordError

    private val _isVerifyingPassword = mutableStateOf(false)
    val isVerifyingPassword by _isVerifyingPassword

    val showPinPad by mutableStateOf(settings.isLockscreenPinPadEnabled())

    fun verifyPassword(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isVerifyingPassword.value = true
            val storedHash = settings.getPasscodeHash()
            val currentHash = HashKeyGenerator.generateHashKey(password)
            val status = currentHash.contentEquals(storedHash)
            if (!status) {
                _password.value = ""
                _passwordError.value = getString(Res.string.incorrect_password)
            }
            onComplete(status)
            _isVerifyingPassword.value = false
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
            logger.e(throwable.message, throwable)
            onComplete(false)
        }
    }

    fun updatePassword(password: String) {
        _passwordError.value = null
        _password.value = password
    }
}