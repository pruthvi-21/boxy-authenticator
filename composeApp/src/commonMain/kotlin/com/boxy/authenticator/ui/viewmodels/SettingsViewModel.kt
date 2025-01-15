package com.boxy.authenticator.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.biometric_prompt_title
import boxy_authenticator.composeapp.generated.resources.cancel
import boxy_authenticator.composeapp.generated.resources.to_disable_biometrics
import boxy_authenticator.composeapp.generated.resources.to_enable_biometrics
import com.boxy.authenticator.helpers.AppSettings
import com.boxy.authenticator.utils.AppTheme
import com.boxy.authenticator.utils.HashUtils
import dev.icerock.moko.biometry.BiometryAuthenticator
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class SettingsViewModel(
    private val settings: AppSettings,
    val biometryAuthenticator: BiometryAuthenticator,
) : ViewModel() {

    private val _appTheme = mutableStateOf(AppTheme.SYSTEM)
    val appTheme: State<AppTheme> = _appTheme

    private val _isAppLockEnabled = mutableStateOf(false)
    val isAppLockEnabled: State<Boolean> = _isAppLockEnabled

    private val _isBiometricUnlockEnabled = mutableStateOf(false)
    val isBiometricUnlockEnabled: State<Boolean> = _isBiometricUnlockEnabled

    private val _isScreenshotsModeEnabled = mutableStateOf(false)
    val isScreenshotsModeEnabled: State<Boolean> = _isScreenshotsModeEnabled

    val showEnableAppLockDialog = mutableStateOf(false)
    val showDisableAppLockDialog = mutableStateOf(false)

    var authPassword = mutableStateOf("")

    init {
        loadSettings()
    }

    fun setAppTheme(theme: AppTheme) {
        settings.setAppTheme(theme)
        _appTheme.value = theme
    }

    private fun loadSettings() {
        _appTheme.value = settings.getAppTheme()
        _isAppLockEnabled.value = settings.isAppLockEnabled()
        _isBiometricUnlockEnabled.value = settings.isBiometricUnlockEnabled()
        _isScreenshotsModeEnabled.value = settings.isScreenshotModeEnabled()
    }

    fun setBiometricUnlockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                promptForBiometrics(
                    title = getString(Res.string.biometric_prompt_title),
                    reason = if (enabled) getString(Res.string.to_enable_biometrics)
                        else getString(Res.string.to_disable_biometrics),
                    failureButtonText = getString(Res.string.cancel),
                    onComplete = {
                        if (it) {
                            settings.setBiometricUnlockEnabled(enabled)
                            _isBiometricUnlockEnabled.value = enabled
                        }
                    }
                )
            } catch (throwable: Throwable) {
                println(throwable)
            }
        }
    }

    fun promptForBiometrics(
        title: String,
        reason: String,
        failureButtonText: String,
        onComplete: (Boolean) -> Unit,
    ) = viewModelScope.launch {
        try {
            val isSuccess = biometryAuthenticator.checkBiometryAuthentication(
                requestTitle = title.desc(),
                requestReason = reason.desc(),
                failureButtonText = failureButtonText.desc(),
                allowDeviceCredentials = false,
            )
            onComplete(isSuccess)
        } catch (throwable: Throwable) {
            println(throwable)
            onComplete(false)
        }
    }

    fun areBiometricsAvailable(): Boolean {
        return biometryAuthenticator.isBiometricAvailable() && isAppLockEnabled.value
    }

//    fun setScreenshotModeEnabled(context: Context, enabled: Boolean) {
//        settings.setScreenshotModeEnabled(enabled)
//        _isScreenshotsModeEnabled.value = enabled
//
//        if (context is MainActivity) {
//            context.setScreenshotMode(enabled)
//        }
//    }

    fun enableAppLock(password: String) {
        viewModelScope.launch {
            try {
                settings.setAppLockEnabled(true, HashUtils.hash(password))
                _isAppLockEnabled.value = true
            } catch (e: IllegalArgumentException) {
                println(e)
            }
        }
    }

    fun disableAppLock(password: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            verifyPassword(password) {
                if (it) {
                    settings.setAppLockEnabled(false)
                    _isAppLockEnabled.value = false

                    settings.setBiometricUnlockEnabled(false)
                    _isBiometricUnlockEnabled.value = false
                }

                onComplete(it)
            }
        }
    }

    fun verifyPassword(password: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val status = HashUtils.verifyHash(
                password,
                settings.getPasscodeHash() ?: ""
            )
            onComplete(status)
        }
    }
}
