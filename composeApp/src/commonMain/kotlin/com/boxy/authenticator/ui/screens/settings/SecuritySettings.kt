package com.boxy.authenticator.ui.screens.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.preference_category_title_security
import boxy_authenticator.composeapp.generated.resources.preference_summary_app_lock
import boxy_authenticator.composeapp.generated.resources.preference_summary_biometrics
import boxy_authenticator.composeapp.generated.resources.preference_summary_block_screenshots
import boxy_authenticator.composeapp.generated.resources.preference_title_app_lock
import boxy_authenticator.composeapp.generated.resources.preference_title_biometrics
import boxy_authenticator.composeapp.generated.resources.preference_title_block_screenshots
import com.boxy.authenticator.ui.viewmodels.LocalSettingsViewModel
import com.boxy.authenticator.Platform
import com.boxy.authenticator.ui.components.dialogs.RemovePasswordDialog
import com.boxy.authenticator.ui.components.dialogs.SetPasswordDialog
import com.jw.preferences.PreferenceCategory
import com.jw.preferences.SwitchPreference
import org.jetbrains.compose.resources.stringResource

@Composable
fun SecuritySettings() {
    val settingsViewModel = LocalSettingsViewModel.current

    val isAppLockEnabled = settingsViewModel.isAppLockEnabled.value
    val isBiometricUnlockEnabled = settingsViewModel.isBiometricUnlockEnabled.value
    val isBlockScreenshotsEnabled = settingsViewModel.isBlockScreenshotsEnabled.value

    PreferenceCategory(
        title = { Text(stringResource(Res.string.preference_category_title_security)) },
    ) {
        SwitchPreference(
            title = { Text(stringResource(Res.string.preference_title_app_lock)) },
            summary = { Text(stringResource(Res.string.preference_summary_app_lock)) },
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
            title = { Text(stringResource(Res.string.preference_title_biometrics)) },
            summary = { Text(stringResource(Res.string.preference_summary_biometrics)) },
            enabled = settingsViewModel.areBiometricsAvailable(),
            value = isBiometricUnlockEnabled,
            onValueChange = {
                settingsViewModel.setBiometricUnlockEnabled(it)
            },
            showDivider = Platform.isAndroid,
        )
        if (Platform.isAndroid) {
            SwitchPreference(
                title = { Text(stringResource(Res.string.preference_title_block_screenshots)) },
                summary = { Text(stringResource(Res.string.preference_summary_block_screenshots)) },
                value = isBlockScreenshotsEnabled,
                onValueChange = {
                    settingsViewModel.setBlockScreenshotsEnabled(it)
                },
                showDivider = false,
            )
        }
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
                settingsViewModel.disableAppLock(password) {
                    settingsViewModel.showDisableAppLockDialog.value = false
                    if (!it) {
                        // TODO: display a toast
                    }
                }
            }
        )
    }

}