package com.boxy.authenticator.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.dialog_message_delete_token
import boxy_authenticator.composeapp.generated.resources.remove
import boxy_authenticator.composeapp.generated.resources.remove_account
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun TokenDeleteDialog(
    issuer: String,
    label: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    TokkyDialog(
        dialogTitle = stringResource(Res.string.remove_account),
        dialogBody = stringResource(Res.string.dialog_message_delete_token, issuer, label),
        confirmText = stringResource(Res.string.remove),
        icon = {
            Icon(
                Icons.Outlined.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        onDismissRequest = onDismiss,
        onConfirmation = onConfirm
    )
}