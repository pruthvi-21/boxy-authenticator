package com.boxy.authenticator.ui.screens.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.preference_category_title_general
import boxy_authenticator.composeapp.generated.resources.preference_summary_use_pin
import boxy_authenticator.composeapp.generated.resources.preference_title_use_pin
import com.boxy.authenticator.ui.viewmodels.SettingsViewModel
import com.boxy.authenticator.utils.TokenTapResponse
import com.jw.preferences.DropDownPreference
import com.jw.preferences.PreferenceCategory
import com.jw.preferences.SwitchPreference
import org.jetbrains.compose.resources.stringResource

@Composable
fun GeneralSettings(settingsViewModel: SettingsViewModel) {
    val isLockscreenPinPadEnabled = settingsViewModel.isLockscreenPinPadEnabled.value

    val tokenTapResponse = settingsViewModel.tokenTapResponse.value
    val tokenTapResponseLabels = listOf(
        "Never",
        "Single tap",
        "Double tap",
        "Long press",
    )

    PreferenceCategory(
        title = { Text(stringResource(Res.string.preference_category_title_general)) },
    ) {
        DropDownPreference(
            title = { Text("Copy tokens to the clipboard") },
            value = tokenTapResponseLabels[tokenTapResponse.ordinal],
            entries = tokenTapResponseLabels,
            summary = {
                Text(
                    text = tokenTapResponseLabels[tokenTapResponse.ordinal],
                    color = MaterialTheme.colorScheme.primary
                )
            },
            onValueChange = {
                val theme = when (it) {
                    tokenTapResponseLabels[1] -> TokenTapResponse.SINGLE_TAP
                    tokenTapResponseLabels[2] -> TokenTapResponse.DOUBLE_TAP
                    tokenTapResponseLabels[3] -> TokenTapResponse.LONG_PRESS
                    else -> TokenTapResponse.NEVER
                }
                settingsViewModel.setTokenTapResponse(theme)
            },
        )
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