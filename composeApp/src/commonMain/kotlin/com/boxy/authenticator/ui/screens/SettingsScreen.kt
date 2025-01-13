package com.boxy.authenticator.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.title_settings
import com.boxy.authenticator.navigation.SettingsScreenComponent
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.preferences.PreferenceScreen
import com.boxy.authenticator.ui.preferences.PreferenceTheme
import com.boxy.authenticator.ui.screens.settings.AppearanceSettings
import com.boxy.authenticator.ui.screens.settings.SecuritySettings
//import com.boxy.authenticator.ui.screens.settings.TransferAccounts
import com.boxy.authenticator.ui.viewmodels.SettingsViewModel
import org.jetbrains.compose.resources.stringResource

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
                categoryShape = MaterialTheme.shapes.medium,
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
