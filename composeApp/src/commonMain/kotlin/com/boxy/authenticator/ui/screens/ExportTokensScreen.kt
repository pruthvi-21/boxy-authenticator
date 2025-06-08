package com.boxy.authenticator.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.accounts_exported
import boxy_authenticator.composeapp.generated.resources.boxy_file
import boxy_authenticator.composeapp.generated.resources.error_fetching_tokens
import boxy_authenticator.composeapp.generated.resources.export
import boxy_authenticator.composeapp.generated.resources.export_accounts
import boxy_authenticator.composeapp.generated.resources.export_failed
import boxy_authenticator.composeapp.generated.resources.export_to
import boxy_authenticator.composeapp.generated.resources.i_understand_the_risk
import boxy_authenticator.composeapp.generated.resources.plain_text_file
import boxy_authenticator.composeapp.generated.resources.recommended
import boxy_authenticator.composeapp.generated.resources.warning
import boxy_authenticator.composeapp.generated.resources.warning_backup_encryption
import boxy_authenticator.composeapp.generated.resources.warning_no_backup_encryption
import com.boxy.authenticator.navigation.LocalNavController
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.components.design.BoxyPreferenceScreen
import com.boxy.authenticator.ui.components.design.BoxyScaffold
import com.boxy.authenticator.ui.components.dialogs.BoxyDialog
import com.boxy.authenticator.ui.components.dialogs.SetPasswordDialog
import com.boxy.authenticator.ui.viewmodels.ExportTokensViewModel
import com.jw.preferences.Preference
import com.jw.preferences.PreferenceCategory
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportTokensScreen() {

    val navController = LocalNavController.current
    val exportViewModel: ExportTokensViewModel = koinViewModel()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        exportViewModel.loadAllTokens()
    }

    BoxyScaffold(
        topBar = {
            Toolbar(
                title = stringResource(Res.string.export_accounts),
                showDefaultNavigationIcon = true,
                onNavigationIconClick = { navController.navigateUp() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { contentPadding ->

        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (exportViewModel.tokensFetchError.value) {
                Text(
                    text = stringResource(Res.string.error_fetching_tokens),
                    color = MaterialTheme.colorScheme.error,
                )
            }
            val exportEnabled =
                !exportViewModel.tokensFetchError.value && exportViewModel.areTokensAvailable

            BoxyPreferenceScreen {
                item {
                    PreferenceCategory(
                        title = { Text(stringResource(Res.string.export_to)) },
                    ) {
                        Preference(
                            title = {
                                Text(
                                    stringResource(Res.string.boxy_file) +
                                            " (${stringResource(Res.string.recommended)})"
                                )
                            },
                            enabled = exportEnabled,
                            onClick = {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                exportViewModel.showSetPasswordDialog.value = true
                            },
                        )
                        Preference(
                            title = { Text(stringResource(Res.string.plain_text_file)) },
                            enabled = exportEnabled,
                            onClick = {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                exportViewModel.showPlainTextWarningDialog.value = true
                            },
                            showDivider = false,
                        )
                    }
                }
            }
        }

        if (exportViewModel.showPlainTextWarningDialog.value) {
            var isUnencryptedAcknowledged by remember { mutableStateOf(false) }

            BoxyDialog(
                dialogTitle = stringResource(Res.string.warning),
                confirmText = stringResource(Res.string.export),
                confirmEnabled = isUnencryptedAcknowledged,
                onDismissRequest = {
                    exportViewModel.showPlainTextWarningDialog.value = false
                },
                onConfirmation = {
                    exportViewModel.showPlainTextWarningDialog.value = false
                    exportViewModel.exportToPlainTextFile {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                if (it) getString(Res.string.accounts_exported)
                                else getString(Res.string.export_failed)
                            )
                        }
                    }
                },
            ) {
                Column {
                    Text(stringResource(Res.string.warning_no_backup_encryption))
                    Row(
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .align(Alignment.End)
                            .clip(MaterialTheme.shapes.small)
                            .clickable { isUnencryptedAcknowledged = !isUnencryptedAcknowledged }
                            .padding(end = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isUnencryptedAcknowledged,
                            onCheckedChange = {
                                isUnencryptedAcknowledged = !isUnencryptedAcknowledged
                            },
                            colors = CheckboxDefaults.colors().copy(
                                uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                            )
                        )

                        Text(stringResource(Res.string.i_understand_the_risk))
                    }
                }
            }
        }

        if (exportViewModel.showSetPasswordDialog.value) {
            SetPasswordDialog(
                dialogBody = stringResource(Res.string.warning_backup_encryption),
                confirmText = stringResource(Res.string.export),
                onDismissRequest = {
                    exportViewModel.showSetPasswordDialog.value = false
                },
                onConfirmation = { password ->
                    exportViewModel.showSetPasswordDialog.value = false
                    exportViewModel.exportToBoxyFile(password) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                if (it) getString(Res.string.accounts_exported)
                                else getString(Res.string.export_failed)
                            )
                        }
                    }
                }
            )
        }
    }
}
