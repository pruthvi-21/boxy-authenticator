package com.ps.tokky.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.ps.tokky.R
import com.ps.tokky.helpers.BiometricsHelper
import com.ps.tokky.ui.components.dialogs.RemovePasswordDialog
import com.ps.tokky.ui.components.dialogs.SetPasswordDialog
import com.ps.tokky.ui.components.preferences.PreferenceCategory
import com.ps.tokky.ui.components.preferences.switchPreference
import com.ps.tokky.ui.viewmodels.SettingsViewModel

@Composable
fun SecuritySettings() {
    val context = LocalContext.current

    val settingsViewModel: SettingsViewModel = hiltViewModel()

    val isAppLockEnabled = settingsViewModel.isAppLockEnabled.value
    val isBiometricUnlockEnabled = settingsViewModel.isBiometricUnlockEnabled.value
    val isScreenshotsModeEnabled = settingsViewModel.isScreenshotsModeEnabled.value

    PreferenceCategory(
        title = stringResource(R.string.preference_category_title_security)
    ) {
        switchPreference(
            title = context.getString(R.string.preference_title_app_lock),
            summary = context.getString(R.string.preference_summary_app_lock),
            checked = isAppLockEnabled,
            onCheckedChange = {
                if (it) {
                    settingsViewModel.showEnableAppLockDialog.value = true
                    settingsViewModel.showDisableAppLockDialog.value = false
                } else {
                    settingsViewModel.showEnableAppLockDialog.value = false
                    settingsViewModel.showDisableAppLockDialog.value = true
                }
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