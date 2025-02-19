package com.boxy.authenticator.ui.components.dialogs

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformAlertDialog(
    title: String?,
    message: String?,
    confirmText: String,
    dismissText: String,
    isDestructive: Boolean,
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    BoxyDialog(
        dialogTitle = title,
        dialogBody = message,
        onDismissRequest = onDismissRequest,
        onConfirmation = onConfirmation,
        isDestructive = isDestructive,
        dismissText = dismissText,
        confirmText = confirmText,
    )
}