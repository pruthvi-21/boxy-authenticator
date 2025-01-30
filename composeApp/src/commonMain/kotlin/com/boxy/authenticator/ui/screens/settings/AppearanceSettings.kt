package com.boxy.authenticator.ui.screens.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.dark
import boxy_authenticator.composeapp.generated.resources.follow_system
import boxy_authenticator.composeapp.generated.resources.light
import boxy_authenticator.composeapp.generated.resources.preference_category_title_appearance
import boxy_authenticator.composeapp.generated.resources.preference_title_app_theme
import com.boxy.authenticator.ui.viewmodels.LocalSettingsViewModel
import com.boxy.authenticator.utils.AppTheme
import com.jw.preferences.DropDownPreference
import com.jw.preferences.PreferenceCategory
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppearanceSettings() {
    val settingsViewModel = LocalSettingsViewModel.current

    val appThemeLabels = listOf(
        stringResource(Res.string.light),
        stringResource(Res.string.dark),
        stringResource(Res.string.follow_system),
    )

    val appTheme = settingsViewModel.appTheme.value

    PreferenceCategory(
        title = { Text(stringResource(Res.string.preference_category_title_appearance)) },
    ) {
        DropDownPreference(
            title = { Text(stringResource(Res.string.preference_title_app_theme)) },
            value = appThemeLabels[appTheme.ordinal],
            entries = appThemeLabels,
            summary = {
                Text(
                    text = appThemeLabels[appTheme.ordinal],
                    color = MaterialTheme.colorScheme.primary
                )
            },
            onValueChange = {
                val theme = when (it) {
                    appThemeLabels[0] -> AppTheme.LIGHT
                    appThemeLabels[1] -> AppTheme.DARK
                    else -> AppTheme.SYSTEM
                }
                settingsViewModel.setAppTheme(theme)
            },
            showDivider = false
        )
    }
}