package com.boxy.authenticator.ui.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.confirm_password
import boxy_authenticator.composeapp.generated.resources.password
import boxy_authenticator.composeapp.generated.resources.set_password
import boxy_authenticator.composeapp.generated.resources.show_password
import com.boxy.authenticator.ui.components.StyledTextField
import com.boxy.authenticator.ui.viewmodels.SetPasswordDialogViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SetPasswordDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
) {

    val focusRequester = remember { FocusRequester() }

    val viewModel: SetPasswordDialogViewModel = koinViewModel()

    TokkyDialog(
        dialogTitle = stringResource(Res.string.set_password),
        onDismissRequest = {
            onDismissRequest()
            viewModel.reset()
        },
        onConfirmation = {
            if (viewModel.validatePasswords()) {
                onConfirmation(viewModel.password)
                viewModel.reset()
            }
        }
    ) {
        Column {
            val focusManager = LocalFocusManager.current
            StyledTextField(
                value = viewModel.password,
                onValueChange = {
                    viewModel.updatePassword(it)
                },
                placeholder = "Enter a password",
                label = stringResource(Res.string.password),
                isPasswordField = !viewModel.showPassword,
                hidePasswordVisibilityEye = true,
                errorMessage = viewModel.passwordError,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.focusRequester(focusRequester)
            )
            Spacer(Modifier.height(10.dp))
            StyledTextField(
                value = viewModel.confirmPassword,
                onValueChange = {
                    viewModel.updateConfirmPassword(it)
                },
                placeholder = "Confirm your password",
                label = stringResource(Res.string.confirm_password),
                isPasswordField = !viewModel.showPassword,
                hidePasswordVisibilityEye = true,
                errorMessage = viewModel.confirmPasswordError,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.clearFocus() }
                ),
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .clickable {
                        viewModel.updateShowPassword(!viewModel.showPassword)
                    }
                    .padding(end = 15.dp)
                    .align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = viewModel.showPassword,
                    onCheckedChange = { viewModel.updateShowPassword(!viewModel.showPassword) },
                )

                Text(stringResource(Res.string.show_password))
            }
        }
    }
}