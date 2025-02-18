package com.boxy.authenticator.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.title_settings
import com.boxy.authenticator.navigation.LocalNavController
import com.boxy.authenticator.navigation.navigateToExportTokens
import com.boxy.authenticator.navigation.navigateToImportTokens
import com.boxy.authenticator.ui.components.BoxyPreferenceScreen
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.screens.settings.AppearanceSettings
import com.boxy.authenticator.ui.screens.settings.GeneralSettings
import com.boxy.authenticator.ui.screens.settings.SecuritySettings
import com.boxy.authenticator.ui.screens.settings.TransferAccounts
import com.boxy.authenticator.ui.viewmodels.LocalSettingsViewModel
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    hideSensitiveSettings: Boolean = false,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val navController = LocalNavController.current
    val settingsViewModel = LocalSettingsViewModel.current
    settingsViewModel.hideSensitiveSettings.value = hideSensitiveSettings

    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(Res.string.title_settings),
                showDefaultNavigationIcon = true,
                onNavigationIconClick = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { contentPadding ->
        BoxyPreferenceScreen(
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 10.dp),
        ) {
            item { AppearanceSettings() }
            item { GeneralSettings() }
            if (!settingsViewModel.hideSensitiveSettings.value) {
                item {
                    SecuritySettings(
                        snackbarHostState = snackbarHostState,
                    )
                }
            }
            if (!settingsViewModel.hideSensitiveSettings.value) {
                item {
                    TransferAccounts(
                        snackbarHostState = snackbarHostState,
                        onNavigateToExport = {
                            navController.navigateToExportTokens()
                        },
                        onNavigateToImport = {
                            navController.navigateToImportTokens()
                        }
                    )
                }
            }
        }
    }
}
