package com.ps.tokky.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ps.tokky.helpers.AppSettings
import com.ps.tokky.helpers.BiometricsHelper
import com.ps.tokky.ui.activities.MainActivity
import com.ps.tokky.utils.AppTheme
import com.ps.tokky.utils.HashUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: AppSettings,
    private val biometricAuthHelper: BiometricsHelper
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

    fun setBiometricUnlockEnabled(context: Context, enabled: Boolean) {
        biometricAuthHelper.authenticate(context) {
            settings.setBiometricUnlockEnabled(enabled)
            _isBiometricUnlockEnabled.value = enabled
        }
    }

    fun setScreenshotModeEnabled(context: Context, enabled: Boolean) {
        settings.setScreenshotModeEnabled(enabled)
        _isScreenshotsModeEnabled.value = enabled

        if (context is MainActivity) {
            context.setScreenshotMode(enabled)
        }
    }

    fun enableAppLock(password: String) {
        settings.setPasscodeHash(HashUtils.hash(password))
        settings.setAppLockEnabled(true)
        _isAppLockEnabled.value = true
    }

    fun disableAppLock(password: String): Boolean {
        val status = HashUtils.verifyString(password, settings.getPasscodeHash() ?: "")
        if (status) {
            settings.setAppLockEnabled(false)
            _isAppLockEnabled.value = false

            settings.setBiometricUnlockEnabled(false)
            _isBiometricUnlockEnabled.value = false
        }

        return status
    }
}
