package com.ps.tokky.ui.screens.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.jw.preferences.PreferenceCategory
import com.jw.preferences.SwitchPreference
import com.ps.tokky.R
import com.ps.tokky.helpers.BiometricsHelper
import com.ps.tokky.ui.components.dialogs.RemovePasswordDialog
import com.ps.tokky.ui.components.dialogs.SetPasswordDialog
import com.ps.tokky.ui.viewmodels.SettingsViewModel

@Composable
fun SecuritySettings(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current

    val isAppLockEnabled = settingsViewModel.isAppLockEnabled.value
    val isBiometricUnlockEnabled = settingsViewModel.isBiometricUnlockEnabled.value
    val isScreenshotsModeEnabled = settingsViewModel.isScreenshotsModeEnabled.value

    PreferenceCategory(
        title = { Text(stringResource(R.string.preference_category_title_security)) },
    ) {
        SwitchPreference(
            title = { Text(stringResource(R.string.preference_title_app_lock)) },
            summary = { Text(stringResource(R.string.preference_summary_app_lock)) },
            value = isAppLockEnabled,
            onValueChange = {
                if (it) {
                    settingsViewModel.showEnableAppLockDialog.value = true
                    settingsViewModel.showDisableAppLockDialog.value = false
                } else {
                    settingsViewModel.showEnableAppLockDialog.value = false
                    settingsViewModel.showDisableAppLockDialog.value = true
                }
            },
        )
        SwitchPreference(
            title = { Text(stringResource(R.string.preference_title_biometrics)) },
            summary = { Text(stringResource(R.string.preference_summary_biometrics)) },
            enabled = BiometricsHelper.areBiometricsAvailable(context) and settingsViewModel.isAppLockEnabled.value,
            value = isBiometricUnlockEnabled,
            onValueChange = {
                settingsViewModel.setBiometricUnlockEnabled(context, it)
            },
        )
        SwitchPreference(
            title = { Text(stringResource(R.string.preference_title_screenshots)) },
            summary = { Text(stringResource(R.string.preference_summary_screenshots)) },
            value = isScreenshotsModeEnabled,
            onValueChange = {
                settingsViewModel.setScreenshotModeEnabled(context, it)
            },
            showDivider = false,
        )
    }

    if (settingsViewModel.showEnableAppLockDialog.value) {
        SetPasswordDialog(
            onDismissRequest = {
                settingsViewModel.showEnableAppLockDialog.value = false
            },
            onConfirmation = { password ->
                settingsViewModel.enableAppLock(password)
                settingsViewModel.showEnableAppLockDialog.value = false
            }
        )
    }

    if (settingsViewModel.showDisableAppLockDialog.value) {
        RemovePasswordDialog(
            onDismissRequest = {
                settingsViewModel.showDisableAppLockDialog.value = false
            },
            onConfirmation = { password ->
                val status = settingsViewModel.disableAppLock(password)
                settingsViewModel.showDisableAppLockDialog.value = false
                if (!status) {
                    // TODO: display a toast
                }
            }
        )
    }

}