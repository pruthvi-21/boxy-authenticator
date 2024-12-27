package com.ps.tokky.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ps.tokky.helpers.AppSettings
import com.ps.tokky.helpers.BiometricsHelper
import com.ps.tokky.utils.HashUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val biometricsHelper: BiometricsHelper,
    private val settings: AppSettings
) : ViewModel() {

    private val _password = mutableStateOf("")
    val password: State<String> get() = _password

    private var onLoginSuccess: (() -> Unit)? = null

    fun registerCallbacks(
        onLoginSuccess: () -> Unit
    ) {
        this.onLoginSuccess = onLoginSuccess
    }

    fun verifyPassword(onFailed: () -> Unit) {
        viewModelScope.launch {
            val status = HashUtils.verifyString(password.value, settings.getPasscodeHash() ?: "")
            if (!status) {
                _password.value = ""
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

    fun updatePassword(password: String) {
        _password.value = password
    }

    companion object {
        private const val TAG = "AuthenticationViewModel"
    }
}
