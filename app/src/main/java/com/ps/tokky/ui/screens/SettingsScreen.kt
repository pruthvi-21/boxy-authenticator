package com.ps.tokky.ui.screens

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jw.preferences.PreferenceScreen
import com.jw.preferences.PreferenceTheme
import com.ps.tokky.R
import com.ps.tokky.ui.components.Toolbar
import com.ps.tokky.ui.screens.settings.SecuritySettings
import com.ps.tokky.ui.screens.settings.TransferAccounts

@Composable
fun SettingsScreen(navController: NavController) {
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
            theme = PreferenceTheme.Default.copy(categoryShape = RoundedCornerShape(15.dp)),
            modifier = Modifier.padding(contentPadding),
        ) {
            item { SecuritySettings() }
            item { TransferAccounts(navController) }
        }
    }
}
