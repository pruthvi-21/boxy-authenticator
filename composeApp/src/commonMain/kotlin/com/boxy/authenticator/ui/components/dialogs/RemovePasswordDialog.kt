package com.boxy.authenticator.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.enter_correct_password
import boxy_authenticator.composeapp.generated.resources.password
import boxy_authenticator.composeapp.generated.resources.remove_password
import com.boxy.authenticator.ui.components.StyledTextField
import com.ps.tokky.ui.viewmodels.RemovePasswordDialogViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun RemovePasswordDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
) {
    val viewModel: RemovePasswordDialogViewModel = koinViewModel()

    val focusRequester = remember { FocusRequester() }

    TokkyDialog(
        dialogTitle = stringResource(Res.string.remove_password),
        onDismissRequest = {
            onDismissRequest()
            viewModel.reset()
        },
        onConfirmation = {
            onConfirmation(viewModel.password)
            viewModel.reset()
        }
    ) {
        Column {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            StyledTextField(
                value = viewModel.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = stringResource(Res.string.password),
                placeholder = stringResource(Res.string.enter_correct_password),
                isPasswordField = true,
                modifier = Modifier.focusRequester(focusRequester),
            )
        }
    }
}