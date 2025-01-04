package com.boxy.authenticator.ui.screens.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.boxy.authenticator.R
import com.boxy.authenticator.ui.viewmodels.SettingsViewModel
import com.boxy.design.constants.AppTheme
import com.boxy.preferences.DropDownPreference
import com.boxy.preferences.PreferenceCategory

@Composable
fun AppearanceSettings(settingsViewModel: SettingsViewModel) {
    val appThemeLabels = listOf(
        stringResource(R.string.light),
        stringResource(R.string.dark),
        stringResource(R.string.follow_system)
    )

    val appTheme = settingsViewModel.appTheme.value

    PreferenceCategory(
        title = { Text(stringResource(R.string.preference_category_title_appearance)) },
    ) {
        DropDownPreference(
            title = { Text(stringResource(R.string.preference_title_app_theme)) },
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