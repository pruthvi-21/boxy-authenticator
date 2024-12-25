package com.ps.tokky.ui.screens

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ps.tokky.R
import com.ps.tokky.ui.components.DefaultAppBarNavigationIcon
import com.ps.tokky.ui.components.TokkyScaffold
import com.ps.tokky.ui.screens.settings.SecuritySettings
import com.ps.tokky.ui.screens.settings.TransferAccounts
import com.ps.tokky.utils.copy
import com.ps.tokky.utils.top

@Composable
fun SettingsScreen(navController: NavController) {

    TokkyScaffold(
        topBar = { Toolbar() }
    ) { contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding)
        ) {
            SecuritySettings()
            TransferAccounts(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar() {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    TopAppBar(
        title = { Text(stringResource(R.string.title_settings)) },
        navigationIcon = {
            DefaultAppBarNavigationIcon {
                backPressedDispatcher?.onBackPressed()
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        windowInsets = WindowInsets.safeDrawing.copy(
            bottom = 0.dp,
            top = WindowInsets.safeDrawing.asPaddingValues()
                .top() + dimensionResource(R.dimen.toolbar_margin_top)
        ),
    )
}