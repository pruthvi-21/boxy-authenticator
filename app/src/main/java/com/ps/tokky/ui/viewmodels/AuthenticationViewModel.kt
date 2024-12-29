package com.ps.tokky.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ps.tokky.helpers.AppSettings
import com.ps.tokky.helpers.BiometricsHelper
import com.ps.tokky.utils.HashUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val biometricsHelper: BiometricsHelper,
    private val settings: AppSettings,
) : ViewModel() {

    private val _password = mutableStateOf("")
    val password: State<String> get() = _password

    fun verifyPassword(): Boolean {
        val status = HashUtils.verifyString(password.value, settings.getPasscodeHash() ?: "")
        if (!status) {
            _password.value = ""
            return false
        }
        return true
    }

    fun areBiometricsEnabled(context: Context): Boolean {
        return BiometricsHelper.areBiometricsAvailable(context) &&
                settings.isBiometricUnlockEnabled()
    }

    fun promptForBiometricsIfAvailable(context: Context, onSuccess: () -> Unit) {
        if (areBiometricsEnabled(context)) {
            biometricsHelper.authenticate(context, onSuccess)
        }
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    companion object {
        private const val TAG = "AuthenticationViewModel"
    }
}
