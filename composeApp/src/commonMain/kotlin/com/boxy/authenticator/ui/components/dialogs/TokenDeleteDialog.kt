package com.boxy.authenticator.ui.components.dialogs

import androidx.compose.runtime.Composable

@Composable
expect fun TokenDeleteDialog(
    issuer: String,
    label: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
)