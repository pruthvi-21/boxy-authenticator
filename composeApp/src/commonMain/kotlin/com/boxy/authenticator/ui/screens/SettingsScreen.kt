package com.boxy.authenticator.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.title_settings
import com.boxy.authenticator.navigation.SettingsScreenComponent
import com.boxy.authenticator.ui.components.BoxSwitch
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.screens.settings.AppearanceSettings
import com.boxy.authenticator.ui.screens.settings.SecuritySettings
//import com.boxy.authenticator.ui.screens.settings.TransferAccounts
import com.boxy.authenticator.ui.viewmodels.SettingsViewModel
import com.jw.preferences.PreferenceScreen
import com.jw.preferences.PreferenceTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    component: SettingsScreenComponent,
    settingsViewModel: SettingsViewModel,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(Res.string.title_settings),
                showDefaultNavigationIcon = true,
                onNavigationIconClick = { component.navigateUp() }
            )
        }
    ) { contentPadding ->
        PreferenceScreen(
            theme = PreferenceTheme.Default.copy(
                preferenceColor = MaterialTheme.colorScheme.surfaceVariant,
                showPreferenceDivider = true,
                categoryContentShape = MaterialTheme.shapes.medium,
                showCategoryDivider = false,
                customSwitch = { checked, enabled ->
                    BoxSwitch(checked = checked, onCheckedChange = null, enabled = enabled)
                }
            ),
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 10.dp),
        ) {
            item { AppearanceSettings(settingsViewModel) }
            item { SecuritySettings(settingsViewModel) }
//            item {
//                TransferAccounts(
//                    onNavigateToExport = {
//                        component.navigateToExport()
//                    },
//                    onNavigateToImport = {
//                        component.navigateToImport()
//                    }
//                )
//            }
        }
    }
}
