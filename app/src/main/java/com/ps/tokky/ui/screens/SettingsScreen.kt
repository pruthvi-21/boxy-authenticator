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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ps.tokky.R
import com.ps.tokky.helpers.BiometricsHelper
import com.ps.tokky.ui.components.DefaultAppBarNavigationIcon
import com.ps.tokky.ui.components.TokkyScaffold
import com.ps.tokky.ui.components.preferences.PreferenceCategory
import com.ps.tokky.ui.components.preferences.switchPreference
import com.ps.tokky.ui.viewmodels.SettingsViewModel
import com.ps.tokky.utils.copy
import com.ps.tokky.utils.top

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current

    val settingsViewModel: SettingsViewModel = hiltViewModel()

    val isAppLockEnabled = settingsViewModel.isAppLockEnabled.value
    val isBiometricUnlockEnabled = settingsViewModel.isBiometricUnlockEnabled.value
    val isScreenshotsModeEnabled = settingsViewModel.isScreenshotsModeEnabled.value

    TokkyScaffold(
        topBar = {
            Toolbar()
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding)
        ) {
            PreferenceCategory(
                title = stringResource(R.string.preference_category_title_security)
            ) {
                switchPreference(
                    title = context.getString(R.string.preference_title_app_lock),
                    summary = context.getString(R.string.preference_summary_app_lock),
                    checked = isAppLockEnabled,
                    onCheckedChange = {
                        settingsViewModel.setAppLockEnabled(context, it)
                    },
                )
                switchPreference(
                    title = context.getString(R.string.preference_title_biometrics),
                    summary = context.getString(R.string.preference_summary_biometrics),
                    checked = isBiometricUnlockEnabled,
                    disabled = !(BiometricsHelper.areBiometricsAvailable(context) and settingsViewModel.isAppLockEnabled.value),
                    onCheckedChange = {
                        settingsViewModel.setBiometricUnlockEnabled(context, it)
                    },
                )
                switchPreference(
                    title = context.getString(R.string.preference_title_screenshots),
                    summary = context.getString(R.string.preference_summary_screenshots),
                    checked = isScreenshotsModeEnabled,
                    onCheckedChange = {
                        settingsViewModel.setScreenshotModeEnabled(context, it)
                    },
                )
            }
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