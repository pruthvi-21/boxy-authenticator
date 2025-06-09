package com.boxy.authenticator.domain.models.form

import com.boxy.authenticator.domain.models.enums.AppTheme
import com.boxy.authenticator.domain.models.enums.TokenTapResponse

sealed class SettingChangeEvent {
    data class AppThemeChanged(val theme: AppTheme) : SettingChangeEvent()
    data class TokenTapResponseChanged(val response: TokenTapResponse) : SettingChangeEvent()
    data class LockScreenPinPadChanged(val enabled: Boolean) : SettingChangeEvent()
    data class BackupAlertsChanged(val enabled: Boolean) : SettingChangeEvent()
    data class AppLockChanged(val enabled: Boolean, val password: String) : SettingChangeEvent()
    data class BiometricUnlockChanged(val enabled: Boolean) : SettingChangeEvent()
    data class BlockScreenshotsChanged(val enabled: Boolean) : SettingChangeEvent()
    data class LockSensitiveFieldsChanged(val enabled: Boolean) : SettingChangeEvent()
}