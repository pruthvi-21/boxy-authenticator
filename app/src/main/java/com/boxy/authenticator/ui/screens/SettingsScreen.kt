package com.boxy.authenticator.ui.screens

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.boxy.authenticator.R
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.screens.settings.AppearanceSettings
import com.boxy.authenticator.ui.screens.settings.SecuritySettings
import com.boxy.authenticator.ui.screens.settings.TransferAccounts
import com.boxy.authenticator.ui.viewmodels.SettingsViewModel
import com.boxy.preferences.PreferenceScreen
import com.boxy.preferences.PreferenceTheme

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    navController: NavController,
) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(R.string.title_settings),
                showDefaultNavigationIcon = true,
                onNavigationIconClick = { backPressedDispatcher?.onBackPressed() }
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
            item { TransferAccounts(navController) }
        }
    }
}
