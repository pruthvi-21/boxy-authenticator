package com.ps.tokky.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jw.design.components.StyledTextField
import com.ps.tokky.R
import com.ps.tokky.ui.viewmodels.RemovePasswordDialogViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RemovePasswordDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
) {
    val viewModel: RemovePasswordDialogViewModel = koinViewModel()

    TokkyDialog(
        dialogTitle = stringResource(R.string.remove_password),
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
            StyledTextField(
                value = viewModel.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = stringResource(R.string.password),
                placeholder = "Enter current password",
                isPasswordField = true,
            )
        }
    }
}