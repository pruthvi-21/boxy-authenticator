package com.boxy.authenticator.ui.state

data class SettingsUiState(
    val settings: SettingsState = SettingsState(),
    val showEnableAppLockDialog: Boolean = false,
    val showDisableAppLockDialog: Boolean = false,
)
