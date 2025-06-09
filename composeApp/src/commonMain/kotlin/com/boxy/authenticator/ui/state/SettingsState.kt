package com.boxy.authenticator.ui.state

import com.boxy.authenticator.domain.models.enums.AppTheme
import com.boxy.authenticator.domain.models.enums.TokenTapResponse

data class SettingsState(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val tokenTapResponse: TokenTapResponse = TokenTapResponse.NEVER,
    val isLockscreenPinPadEnabled: Boolean = false,
    val isDisableBackupAlertsEnabled: Boolean = false,
    val isAppLockEnabled: Boolean = false,
    val isBiometricUnlockEnabled: Boolean = false,
    val isBlockScreenshotsEnabled: Boolean = false,
    val isLockSensitiveFieldsEnabled: Boolean = true,
    val lastBackupTimestamp: Long = 0L
)