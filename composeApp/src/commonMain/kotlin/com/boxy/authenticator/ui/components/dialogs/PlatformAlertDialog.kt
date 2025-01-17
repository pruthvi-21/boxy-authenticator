package com.boxy.authenticator.ui.components.dialogs

import androidx.compose.runtime.Composable
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.cancel
import boxy_authenticator.composeapp.generated.resources.ok
import org.jetbrains.compose.resources.stringResource

@Composable
expect fun PlatformAlertDialog(
    title: String? = null,
    message: String? = null,
    confirmText: String = stringResource(Res.string.ok),
    dismissText: String = stringResource(Res.string.cancel),
    isDestructive: Boolean = false,
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
)