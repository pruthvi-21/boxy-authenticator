package com.boxy.authenticator.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.biometric_prompt_title
import boxy_authenticator.composeapp.generated.resources.cancel
import boxy_authenticator.composeapp.generated.resources.to_disable_biometrics
import boxy_authenticator.composeapp.generated.resources.to_disable_this_setting
import boxy_authenticator.composeapp.generated.resources.to_enable_biometrics
import boxy_authenticator.composeapp.generated.resources.verify_your_identity
import com.boxy.authenticator.core.AppSettings
import com.boxy.authenticator.core.Logger
import com.boxy.authenticator.core.crypto.HashKeyGenerator
import com.boxy.authenticator.domain.models.enums.AppTheme
import com.boxy.authenticator.domain.models.enums.TokenTapResponse
import dev.icerock.moko.biometry.BiometryAuthenticator
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class SettingsViewModel(
    private val settings: AppSettings,
    val biometryAuthenticator: BiometryAuthenticator,
) : ViewModel() {
    private val logger = Logger("SettingsViewModel")

    val hideSensitiveSettings = mutableStateOf(false)

    private val _appTheme = mutableStateOf(AppTheme.SYSTEM)
    val appTheme: State<AppTheme> = _appTheme

    private val _tokenTapResponse = mutableStateOf(TokenTapResponse.NEVER)
    val tokenTapResponse: State<TokenTapResponse> = _tokenTapResponse

    private val _isLockscreenPinPadEnabled = mutableStateOf(false)
    val isLockscreenPinPadEnabled: State<Boolean> = _isLockscreenPinPadEnabled

    private val _isDisableBackupAlertsEnabled = mutableStateOf(false)
    val isDisableBackupAlertsEnabled by _isDisableBackupAlertsEnabled

    private val _isAppLockEnabled = mutableStateOf(false)
    val isAppLockEnabled: State<Boolean> = _isAppLockEnabled

    private val _isBiometricUnlockEnabled = mutableStateOf(false)
    val isBiometricUnlockEnabled: State<Boolean> = _isBiometricUnlockEnabled

    private val _isBlockScreenshotsEnabled = mutableStateOf(false)
    val isBlockScreenshotsEnabled: State<Boolean> = _isBlockScreenshotsEnabled

    private val _isLockSensitiveFieldsEnabled = mutableStateOf(true)
    val isLockSensitiveFieldsEnabled: State<Boolean> = _isLockSensitiveFieldsEnabled

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
        _isDisableBackupAlertsEnabled.value = settings.isDisableBackupAlertsEnabled()

        //Security
        _isAppLockEnabled.value = settings.isAppLockEnabled()
        _isBiometricUnlockEnabled.value = settings.isBiometricUnlockEnabled()
        _isBlockScreenshotsEnabled.value = settings.isBlockScreenshotsEnabled()
        _isLockSensitiveFieldsEnabled.value = settings.isLockSensitiveFieldsEnabled()
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
                logger.e(throwable.message, throwable)
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
            logger.e(throwable.message, throwable)
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

    fun setLockSensitiveFieldsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (!enabled) {
                if (biometryAuthenticator.isBiometricAvailable()) {
                    promptForBiometrics(
                        title = getString(Res.string.verify_your_identity),
                        reason = getString(Res.string.to_disable_this_setting),
                        failureButtonText = getString(Res.string.cancel),
                        onComplete = {
                            if (it) applyLockSensitiveSetting(false)
                        }
                    )
                } else {
                    applyLockSensitiveSetting(false)
                }
            } else {
                applyLockSensitiveSetting(true)
            }
        }
    }

    private fun applyLockSensitiveSetting(enabled: Boolean) {
        settings.setLockSensitiveFieldsEnabled(enabled)
        _isLockSensitiveFieldsEnabled.value = enabled
    }

    fun setLockscreenPinPadEnabled(enabled: Boolean) {
        settings.setLockscreenPinPadEnabled(enabled)
        _isLockscreenPinPadEnabled.value = enabled
    }

    fun setDisableBackupAlertsEnabled(enabled: Boolean) {
        settings.setDisableBackupAlertsEnabled(enabled)
        _isDisableBackupAlertsEnabled.value = enabled
    }

    fun enableAppLock(password: String) {
        viewModelScope.launch {
            try {
                settings.setAppLockEnabled(
                    true,
                    HashKeyGenerator.generateHashKey(password)
                )
                _isAppLockEnabled.value = true
            } catch (e: IllegalArgumentException) {
                logger.e(e.message, e)
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

    private fun verifyPassword(password: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val storedHash = settings.getPasscodeHash()
            val currentHash = HashKeyGenerator.generateHashKey(password)
            val status = currentHash.contentEquals(storedHash)
            onComplete(status)
        }
    }

    fun setTokenTapResponse(response: TokenTapResponse) {
        settings.setTokenTapResponse(response)
        _tokenTapResponse.value = response
    }
}

val LocalSettingsViewModel = staticCompositionLocalOf<SettingsViewModel> {
    error("SettingsViewModel not provided")
}
