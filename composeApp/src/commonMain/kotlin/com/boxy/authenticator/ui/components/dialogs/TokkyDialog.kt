package com.boxy.authenticator.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
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
    confirmText: String = stringResource(Res.string.ok),
    dismissText: String = stringResource(Res.string.cancel),
    confirmEnabled: Boolean = true,
    isDestructive: Boolean = false,
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
        BoxWithConstraints {
            val width = minOf(maxWidth * 0.85f, 500.dp)
            Column(
                modifier = Modifier
                    .width(width)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(top = 18.dp, bottom = 10.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                if (dialogTitle != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 54.dp)
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = dialogTitle,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W500,
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    if (!dialogBody.isNullOrEmpty()) {
                        Text(
                            text = dialogBody,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 5.dp)
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
                        .align(Alignment.End),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TokkyTextButton(
                        onClick = { onDismissRequest() },
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.widthIn(min = 70.dp)
                    ) {
                        Text(text = dismissText)
                    }

                    TokkyTextButton(
                        onClick = { onConfirmation() },
                        enabled = confirmEnabled,
                        shape = MaterialTheme.shapes.extraSmall,
                        colors = ButtonDefaults.textButtonColors().copy(
                            contentColor = if (isDestructive) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.widthIn(min = 70.dp)
                    ) {
                        Text(text = confirmText)
                    }
                }
            }
        }
    }
}