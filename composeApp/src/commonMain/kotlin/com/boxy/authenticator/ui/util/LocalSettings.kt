package com.boxy.authenticator.ui.util

import androidx.compose.runtime.staticCompositionLocalOf
import com.boxy.authenticator.ui.state.SettingsState

val LocalSettings = staticCompositionLocalOf<SettingsState> {
    error("SettingsState not provided")
}