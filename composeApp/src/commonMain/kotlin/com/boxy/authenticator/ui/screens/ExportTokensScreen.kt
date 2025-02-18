package com.boxy.authenticator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.confirm_password
import boxy_authenticator.composeapp.generated.resources.encrypt_backup
import boxy_authenticator.composeapp.generated.resources.enter_password_for_backup
import boxy_authenticator.composeapp.generated.resources.export
import boxy_authenticator.composeapp.generated.resources.export_accounts
import boxy_authenticator.composeapp.generated.resources.i_understand_the_risk
import boxy_authenticator.composeapp.generated.resources.password
import boxy_authenticator.composeapp.generated.resources.reenter_above_password
import boxy_authenticator.composeapp.generated.resources.show_password
import boxy_authenticator.composeapp.generated.resources.warning_backup_encryption
import boxy_authenticator.composeapp.generated.resources.warning_no_backup_encryption
import com.boxy.authenticator.navigation.LocalNavController
import com.boxy.authenticator.ui.components.BoxSwitch
import com.boxy.authenticator.ui.components.StyledTextField
import com.boxy.authenticator.ui.components.TokkyButton
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.viewmodels.ExportTokensViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun ExportTokensScreen() {

    val navController = LocalNavController.current
    val exportViewModel: ExportTokensViewModel = koinViewModel()

    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(Res.string.export_accounts),
                showDefaultNavigationIcon = true,
                onNavigationIconClick = { navController.navigateUp() }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { exportViewModel.toggleIsEncrypted() }
                    .padding(vertical = 8.dp, horizontal = 15.dp)
                    .heightIn(42.dp),
            ) {
                Text(stringResource(Res.string.encrypt_backup))
                Spacer(Modifier.weight(1f))
                BoxSwitch(
                    checked = exportViewModel.isEncrypted,
                    onCheckedChange = { exportViewModel.toggleIsEncrypted() }
                )
            }

            AnimatedVisibility(
                visible = !exportViewModel.isEncrypted,
                content = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(vertical = 10.dp),
                    ) {
                        WarningText(stringResource(Res.string.warning_no_backup_encryption))
                        Checkbox(
                            checked = exportViewModel.isUnencryptedAcknowledged,
                            toggle = {
                                exportViewModel.toggleIsUnencryptedAcknowledged()
                            },
                            label = stringResource(Res.string.i_understand_the_risk),
                        )
                    }
                }
            )

            AnimatedVisibility(
                visible = exportViewModel.isEncrypted,
                content = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(vertical = 10.dp),
                    ) {
                        WarningText(stringResource(Res.string.warning_backup_encryption))

                        val focusManager = LocalFocusManager.current
                        StyledTextField(
                            value = exportViewModel.password,
                            onValueChange = { exportViewModel.updatePassword(it) },
                            label = stringResource(Res.string.password),
                            placeholder = stringResource(Res.string.enter_password_for_backup),
                            isPasswordField = !exportViewModel.showPassword,
                            hidePasswordVisibilityEye = true,
                            errorMessage = exportViewModel.passwordError,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                        )
                        StyledTextField(
                            value = exportViewModel.confirmPassword,
                            onValueChange = { exportViewModel.updateConfirmPassword(it) },
                            label = stringResource(Res.string.confirm_password),
                            placeholder = stringResource(Res.string.reenter_above_password),
                            isPasswordField = !exportViewModel.showPassword,
                            hidePasswordVisibilityEye = true,
                            errorMessage = exportViewModel.confirmPasswordError,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.clearFocus() }
                            ),
                        )
                        Checkbox(
                            checked = exportViewModel.showPassword,
                            toggle = {
                                exportViewModel.toggleShowPassword()
                            },
                            label = stringResource(Res.string.show_password),
                            modifier = Modifier.align(Alignment.End),
                        )
                    }
                }
            )

            Spacer(Modifier.weight(1f))
            TokkyButton(
                onClick = {
                    exportViewModel.exportTokensToFile {
                        navController.navigateUp()
                    }
                },
                enabled = exportViewModel.isExportEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .heightIn(min = 46.dp)
            ) {
                Text(stringResource(Res.string.export))
            }
        }
    }
}

@Composable
private fun WarningText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
    )
}

@Composable
private fun Checkbox(
    checked: Boolean,
    toggle: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable { toggle() }
            .padding(end = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { toggle() },
            colors = CheckboxDefaults.colors().copy(
                uncheckedBorderColor = MaterialTheme.colorScheme.outline,
            )
        )

        Text(label)
    }
}