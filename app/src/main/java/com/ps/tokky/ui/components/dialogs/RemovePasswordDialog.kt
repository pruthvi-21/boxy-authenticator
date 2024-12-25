package com.ps.tokky.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ps.tokky.R
import com.ps.tokky.ui.components.StyledTextField
import com.ps.tokky.ui.components.TokkyDialog
import com.ps.tokky.ui.viewmodels.RemovePasswordDialogViewModel

@Composable
fun RemovePasswordDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
) {
    val viewModel: RemovePasswordDialogViewModel = hiltViewModel()

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
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
            )
            Spacer(Modifier.height(10.dp))
        }
    }
}