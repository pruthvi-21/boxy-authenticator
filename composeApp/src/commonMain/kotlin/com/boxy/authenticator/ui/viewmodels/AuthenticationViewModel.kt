package com.boxy.authenticator.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.biometric_prompt_title
import boxy_authenticator.composeapp.generated.resources.cancel
import boxy_authenticator.composeapp.generated.resources.incorrect_password
import boxy_authenticator.composeapp.generated.resources.to_unlock
import com.boxy.authenticator.core.SettingsDataStore
import com.boxy.authenticator.core.Logger
import com.boxy.authenticator.core.crypto.HashKeyGenerator
import com.boxy.authenticator.ui.state.AuthenticationUiState
import dev.icerock.moko.biometry.BiometryAuthenticator
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class AuthenticationViewModel(
    private val settings: SettingsDataStore,
) : ViewModel() {
    private val logger = Logger("AuthenticationViewModel")

    private val _uiState = MutableStateFlow(AuthenticationUiState())
    val uiState = _uiState.asStateFlow()

    val isPinPadVisible = mutableStateOf(settings.isLockscreenPinPadEnabled())

    fun updatePinPadVisibility() {
        isPinPadVisible.value = settings.isLockscreenPinPadEnabled()
    }

    fun verifyPassword(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isVerifyingPassword = true
            )
            val storedHash = settings.getPasscodeHash()
            val currentHash = HashKeyGenerator.generateHashKey(uiState.value.password)
            val status = currentHash.contentEquals(storedHash)
            if (!status) {
                _uiState.value = _uiState.value.copy(
                    password = "",
                    passwordError = getString(Res.string.incorrect_password)
                )
            }
            onComplete(status)
            _uiState.value = _uiState.value.copy(
                isVerifyingPassword = false
            )
        }
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null,
        )
    }
}