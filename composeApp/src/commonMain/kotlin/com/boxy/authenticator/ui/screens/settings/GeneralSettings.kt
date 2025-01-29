package com.boxy.authenticator.ui.screens.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.preference_category_title_general
import boxy_authenticator.composeapp.generated.resources.preference_summary_use_pin
import boxy_authenticator.composeapp.generated.resources.preference_title_use_pin
import com.boxy.authenticator.ui.viewmodels.SettingsViewModel
import com.jw.preferences.PreferenceCategory
import com.jw.preferences.SwitchPreference
import org.jetbrains.compose.resources.stringResource

@Composable
fun GeneralSettings(settingsViewModel: SettingsViewModel) {
    val isLockscreenPinPadEnabled = settingsViewModel.isLockscreenPinPadEnabled.value

    PreferenceCategory(
        title = { Text(stringResource(Res.string.preference_category_title_general)) },
    ) {
        SwitchPreference(
            title = { Text(stringResource(Res.string.preference_title_use_pin)) },
            summary = { Text(stringResource(Res.string.preference_summary_use_pin)) },
            enabled = settingsViewModel.isAppLockEnabled.value,
            value = isLockscreenPinPadEnabled,
            onValueChange = {
                settingsViewModel.setLockscreenPinPadEnabled(it)
            },
            showDivider = false,
        )
    }
}