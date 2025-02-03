package com.boxy.authenticator.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.biometric_prompt_title
import boxy_authenticator.composeapp.generated.resources.cancel
import boxy_authenticator.composeapp.generated.resources.to_disable_biometrics
import boxy_authenticator.composeapp.generated.resources.to_enable_biometrics
import com.boxy.authenticator.helpers.AppSettings
import com.boxy.authenticator.helpers.Logger
import com.boxy.authenticator.utils.AppTheme
import com.boxy.authenticator.utils.HashUtils
import com.boxy.authenticator.utils.TokenTapResponse
import dev.icerock.moko.biometry.BiometryAuthenticator
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class SettingsViewModel(
    private val settings: AppSettings,
    val biometryAuthenticator: BiometryAuthenticator,
) : ViewModel() {

    val hideSensitiveSettings = mutableStateOf(false)

    private val _appTheme = mutableStateOf(AppTheme.SYSTEM)
    val appTheme: State<AppTheme> = _appTheme

    private val _tokenTapResponse = mutableStateOf(TokenTapResponse.NEVER)
    val tokenTapResponse: State<TokenTapResponse> = _tokenTapResponse

    private val _isLockscreenPinPadEnabled = mutableStateOf(false)
    val isLockscreenPinPadEnabled: State<Boolean> = _isLockscreenPinPadEnabled

    private val _isAppLockEnabled = mutableStateOf(false)
    val isAppLockEnabled: State<Boolean> = _isAppLockEnabled

    private val _isBiometricUnlockEnabled = mutableStateOf(false)
    val isBiometricUnlockEnabled: State<Boolean> = _isBiometricUnlockEnabled

    private val _isBlockScreenshotsEnabled = mutableStateOf(false)
    val isBlockScreenshotsEnabled: State<Boolean> = _isBlockScreenshotsEnabled

    val showEnableAppLockDialog = mutableStateOf(false)
    val showDisableAppLockDialog = mutableStateOf(false)

    init {
        loadSettings()
    }

    fun setAppTheme(theme: AppTheme) {
        settings.setAppTheme(theme)
        _appTheme.value = theme
    }

    private fun loadSettings() {
        //Appearance
        _appTheme.value = settings.getAppTheme()

        //General
        _tokenTapResponse.value = settings.getTokenTapResponse()
        _isLockscreenPinPadEnabled.value = settings.isLockscreenPinPadEnabled()

        //Security
        _isAppLockEnabled.value = settings.isAppLockEnabled()
        _isBiometricUnlockEnabled.value = settings.isBiometricUnlockEnabled()
        _isBlockScreenshotsEnabled.value = settings.isBlockScreenshotsEnabled()
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
                Logger.e(TAG, throwable.message, throwable)
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
            Logger.e(TAG, throwable.message, throwable)
            onComplete(false)
        }
    }

    fun areBiometricsAvailable(): Boolean {
        return biometryAuthenticator.isBiometricAvailable() && isAppLockEnabled.value
    }

    fun setBlockScreenshotsEnabled(enabled: Boolean) {
        settings.setBlockScreenshotsEnabled(enabled)
        _isBlockScreenshotsEnabled.value = enabled
    }

    fun setLockscreenPinPadEnabled(enabled: Boolean) {
        settings.setLockscreenPinPadEnabled(enabled)
        _isLockscreenPinPadEnabled.value = enabled
    }

    fun enableAppLock(password: String) {
        viewModelScope.launch {
            try {
                settings.setAppLockEnabled(true, HashUtils.hash(password))
                _isAppLockEnabled.value = true
            } catch (e: IllegalArgumentException) {
                Logger.e(TAG, e.message, e)
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

    fun setTokenTapResponse(response: TokenTapResponse) {
        settings.setTokenTapResponse(response)
        _tokenTapResponse.value = response
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}

val LocalSettingsViewModel = staticCompositionLocalOf<SettingsViewModel> {
    error("SettingsViewModel not provided")
}
