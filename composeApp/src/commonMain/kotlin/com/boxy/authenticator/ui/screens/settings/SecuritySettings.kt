package com.boxy.authenticator.ui.screens.settings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.enter_correct_password
import boxy_authenticator.composeapp.generated.resources.incorrect_password
import boxy_authenticator.composeapp.generated.resources.password
import boxy_authenticator.composeapp.generated.resources.preference_category_title_security
import boxy_authenticator.composeapp.generated.resources.preference_summary_app_lock
import boxy_authenticator.composeapp.generated.resources.preference_summary_biometrics
import boxy_authenticator.composeapp.generated.resources.preference_summary_block_screenshots
import boxy_authenticator.composeapp.generated.resources.preference_summary_lock_sensitive_fields
import boxy_authenticator.composeapp.generated.resources.preference_title_app_lock
import boxy_authenticator.composeapp.generated.resources.preference_title_biometrics
import boxy_authenticator.composeapp.generated.resources.preference_title_block_screenshots
import boxy_authenticator.composeapp.generated.resources.preference_title_lock_sensitive_fields
import boxy_authenticator.composeapp.generated.resources.remove_password
import com.boxy.authenticator.core.Platform
import com.boxy.authenticator.ui.components.dialogs.RequestPasswordDialog
import com.boxy.authenticator.ui.components.dialogs.SetPasswordDialog
import com.boxy.authenticator.ui.viewmodels.LocalSettingsViewModel
import com.jw.preferences.PreferenceCategory
import com.jw.preferences.SwitchPreference
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun SecuritySettings(
    snackbarHostState: SnackbarHostState,
) {
    val settingsViewModel = LocalSettingsViewModel.current

    val isAppLockEnabled = settingsViewModel.isAppLockEnabled.value
    val isBiometricUnlockEnabled = settingsViewModel.isBiometricUnlockEnabled.value
    val isBlockScreenshotsEnabled = settingsViewModel.isBlockScreenshotsEnabled.value
    val isLockSensitiveFieldsEnabled = settingsViewModel.isLockSensitiveFieldsEnabled.value

    val scope = rememberCoroutineScope()

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
                    snackbarHostState.currentSnackbarData?.dismiss()
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
        )
        if (Platform.isAndroid) {
            SwitchPreference(
                title = { Text(stringResource(Res.string.preference_title_block_screenshots)) },
                summary = { Text(stringResource(Res.string.preference_summary_block_screenshots)) },
                value = isBlockScreenshotsEnabled,
                onValueChange = {
                    settingsViewModel.setBlockScreenshotsEnabled(it)
                },
            )
        }
        SwitchPreference(
            title = { Text(stringResource(Res.string.preference_title_lock_sensitive_fields)) },
            summary = { Text(stringResource(Res.string.preference_summary_lock_sensitive_fields)) },
            value = isLockSensitiveFieldsEnabled,
            onValueChange = {
                settingsViewModel.setLockSensitiveFieldsEnabled(it)
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
        RequestPasswordDialog(
            title = stringResource(Res.string.remove_password),
            label = stringResource(Res.string.password),
            placeholder = stringResource(Res.string.enter_correct_password),
            onDismissRequest = {
                settingsViewModel.showDisableAppLockDialog.value = false
            },
            onConfirmation = { password ->
                settingsViewModel.disableAppLock(password) {
                    settingsViewModel.showDisableAppLockDialog.value = false
                    if (!it) {
                        scope.launch {
                            snackbarHostState.showSnackbar(getString(Res.string.incorrect_password))
                        }
                    }
                }
            }
        )
    }

}