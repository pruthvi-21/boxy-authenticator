package com.boxy.authenticator.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.cancel
import boxy_authenticator.composeapp.generated.resources.ok
import com.boxy.authenticator.ui.components.TokkyTextButton
import org.jetbrains.compose.resources.stringResource

@Composable
fun TokkyDialog(
    dialogTitle: String? = null,
    dialogBody: String? = null,
    icon: (@Composable () -> Unit)? = null,
    confirmText: String = stringResource(Res.string.ok),
    dismissText: String = stringResource(Res.string.cancel),
    confirmEnabled: Boolean = true,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    content: (@Composable () -> Unit)? = null,
) {
    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        ),
        onDismissRequest = {
            onDismissRequest()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .widthIn(max = 500.dp)
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Spacer(Modifier.height(18.dp))
            if (dialogTitle != null || icon != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 54.dp)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    icon?.invoke()
                    if (dialogTitle != null) {
                        Spacer(Modifier.height(if (icon != null) 5.dp else 10.dp))
                        Text(
                            text = dialogTitle,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    if (!dialogBody.isNullOrEmpty()) {
                        Text(
                            text = dialogBody,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 15.dp)
                        )
                    }

                    if (content != null) {
                        Box(Modifier.padding(vertical = 5.dp)) {
                            content.invoke()
                        }
                    }
                }

                Row(
                    Modifier
                        .padding(vertical = 4.dp, horizontal = 12.dp)
                        .heightIn(min = 54.dp)
                ) {
                    Spacer(Modifier.weight(1f))
                    TokkyTextButton(
                        onClick = { onDismissRequest() },
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.widthIn(min = 70.dp)
                    ) {
                        Text(text = dismissText)
                    }
                    Spacer(Modifier.width(5.dp))
                    TokkyTextButton(
                        onClick = { onConfirmation() },
                        enabled = confirmEnabled,
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.widthIn(min = 70.dp)
                    ) {
                        Text(text = confirmText)
                    }
                }
            }
        }
    }
}