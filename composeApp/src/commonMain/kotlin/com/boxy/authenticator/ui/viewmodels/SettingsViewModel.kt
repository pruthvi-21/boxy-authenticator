package com.boxy.authenticator.ui.viewmodels

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
import com.boxy.authenticator.domain.models.form.SettingChangeEvent
import com.boxy.authenticator.ui.state.SettingsState
import com.boxy.authenticator.ui.state.SettingsUiState
import dev.icerock.moko.biometry.BiometryAuthenticator
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class SettingsViewModel(
    private val settings: AppSettings,
    val biometryAuthenticator: BiometryAuthenticator,
) : ViewModel() {
    private val logger = Logger("SettingsViewModel")

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun onEvent(event: SettingChangeEvent) {
        when (event) {
            is SettingChangeEvent.AppThemeChanged -> {
                settings.setAppTheme(event.theme)
                updateSettings(
                    _uiState.value.settings.copy(appTheme = event.theme)
                )
            }

            is SettingChangeEvent.TokenTapResponseChanged -> {
                settings.setTokenTapResponse(event.response)
                updateSettings(
                    _uiState.value.settings.copy(tokenTapResponse = event.response)
                )
            }

            is SettingChangeEvent.LockScreenPinPadChanged -> {
                settings.setLockscreenPinPadEnabled(event.enabled)
                updateSettings(
                    _uiState.value.settings.copy(isLockscreenPinPadEnabled = event.enabled)
                )
            }

            is SettingChangeEvent.BackupAlertsChanged -> {
                settings.setDisableBackupAlertsEnabled(event.enabled)
                updateSettings(
                    _uiState.value.settings.copy(isDisableBackupAlertsEnabled = event.enabled)
                )
            }

            is SettingChangeEvent.AppLockChanged -> {
                if (event.enabled) enableAppLock(event.password)
                else disableAppLock(event.password, {})
            }

            is SettingChangeEvent.BiometricUnlockChanged -> {
//                toggleBiometrics(event.enabled)
            }
            is SettingChangeEvent.BlockScreenshotsChanged -> {
                settings.setBlockScreenshotsEnabled(event.enabled)
                updateSettings(
                    _uiState.value.settings.copy(isBlockScreenshotsEnabled = event.enabled)
                )
            }

            is SettingChangeEvent.LockSensitiveFieldsChanged -> {
                settings.setLockSensitiveFieldsEnabled(event.enabled)
                updateSettings(
                    _uiState.value.settings.copy(isLockSensitiveFieldsEnabled = event.enabled)
                )
            }
        }
    }

    private fun loadSettings() {
        updateSettings(
            _uiState.value.settings.copy(
                //Appearance
                appTheme = settings.getAppTheme(),

                //General
                tokenTapResponse = settings.getTokenTapResponse(),
                isLockscreenPinPadEnabled = settings.isLockscreenPinPadEnabled(),
                isDisableBackupAlertsEnabled = settings.isDisableBackupAlertsEnabled(),

                //Security
                isAppLockEnabled = settings.isAppLockEnabled(),
                isBiometricUnlockEnabled = settings.isBiometricUnlockEnabled(),
                isBlockScreenshotsEnabled = settings.isBlockScreenshotsEnabled(),
                isLockSensitiveFieldsEnabled = settings.isLockSensitiveFieldsEnabled(),

                lastBackupTimestamp = settings.getLastBackupTimestamp(),
            )
        )
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
                            updateSettings(
                                _uiState.value.settings.copy(isBiometricUnlockEnabled = enabled)
                            )
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
        return biometryAuthenticator.isBiometricAvailable() && _uiState.value.settings.isAppLockEnabled
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
                            if (it) onEvent(SettingChangeEvent.LockSensitiveFieldsChanged(false))
                        }
                    )
                } else {
                    onEvent(SettingChangeEvent.LockSensitiveFieldsChanged(false))
                }
            } else {
                onEvent(SettingChangeEvent.LockSensitiveFieldsChanged(true))
            }
        }
    }

    fun enableAppLock(password: String) {
        viewModelScope.launch {
            try {
                settings.setAppLockEnabled(
                    true,
                    HashKeyGenerator.generateHashKey(password)
                )
                updateSettings(
                    _uiState.value.settings.copy(isAppLockEnabled = true)
                )
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
                    settings.setBiometricUnlockEnabled(false)

                    updateSettings(
                        _uiState.value.settings.copy(
                            isAppLockEnabled = false,
                            isBiometricUnlockEnabled = false,
                        )
                    )
                }

                onComplete(it)
            }
        }
    }

    private fun updateSettings(settings: SettingsState) {
        _uiState.value = _uiState.value.copy(
            settings = settings
        )
    }


    private fun verifyPassword(password: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val storedHash = settings.getPasscodeHash()
            val currentHash = HashKeyGenerator.generateHashKey(password)
            val status = currentHash.contentEquals(storedHash)
            onComplete(status)
        }
    }

    fun showEnableAppLockDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(
            showEnableAppLockDialog = show
        )
    }

    fun showDisableAppLockDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(
            showEnableAppLockDialog = show
        )
    }
}

val LocalSettingsViewModel = staticCompositionLocalOf<SettingsViewModel> {
    error("SettingsViewModel not provided")
}
