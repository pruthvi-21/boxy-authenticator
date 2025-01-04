package com.boxy.authenticator.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.boxy.authenticator.R

@Composable
fun TokenDeleteDialog(
    issuer: String,
    label: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    TokkyDialog(
        dialogTitle = stringResource(R.string.remove_account),
        dialogBody = stringResource(
            R.string.dialog_message_delete_token,
            "${issuer}${if (label.isNotBlank()) " (${label})" else ""}"
        ),
        confirmText = stringResource(R.string.remove),
        icon = {
            Icon(
                Icons.Outlined.WarningAmber,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        onDismissRequest = onDismiss,
        onConfirmation = onConfirm
    )
}