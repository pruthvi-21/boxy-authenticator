package com.boxy.authenticator.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.title_settings
import com.boxy.authenticator.navigation.components.SettingsScreenComponent
import com.boxy.authenticator.ui.components.BoxSwitch
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.screens.settings.AppearanceSettings
import com.boxy.authenticator.ui.screens.settings.GeneralSettings
import com.boxy.authenticator.ui.screens.settings.SecuritySettings
import com.boxy.authenticator.ui.screens.settings.TransferAccounts
import com.boxy.authenticator.ui.viewmodels.LocalSettingsViewModel
import com.jw.preferences.PreferenceScreen
import com.jw.preferences.PreferenceTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    component: SettingsScreenComponent,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val settingsViewModel = LocalSettingsViewModel.current
    settingsViewModel.hideSensitiveSettings.value = component.hideSensitiveSettings

    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(Res.string.title_settings),
                showDefaultNavigationIcon = true,
                onNavigationIconClick = { component.navigateUp() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            item { AppearanceSettings() }
            item { GeneralSettings() }
            if (!settingsViewModel.hideSensitiveSettings.value) {
                item { SecuritySettings() }
            }
            if (!settingsViewModel.hideSensitiveSettings.value) {
                item {
                    TransferAccounts(
                        snackbarHostState = snackbarHostState,
                        onNavigateToExport = {
                            component.navigateToExport()
                        },
                        onNavigateToImport = {
                            component.navigateToImport(it)
                        }
                    )
                }
            }
        }
    }
}
