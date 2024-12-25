package com.ps.tokky.ui.components.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.ps.tokky.R

@Composable
fun TokkyDialog(
    dialogTitle: String? = null,
    dialogBody: String? = null,
    icon: (@Composable () -> Unit)? = null,
    confirmText: String = stringResource(R.string.ok),
    dismissText: String = stringResource(R.string.cancel),
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    content: (@Composable () -> Unit)? = null,
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .widthIn(max = 500.dp),
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true,
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        ),
        shape = RoundedCornerShape(16.dp),
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(onClick = { onConfirmation() }) {
                Text(text = confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = dismissText)
            }
        },
        icon = { icon?.invoke() },
        title = if (dialogTitle != null) {
            { Text(text = dialogTitle, fontSize = 18.sp) }
        } else null,
        text = {
            Column {
                if (!dialogBody.isNullOrEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Text(text = dialogBody)
                }

                if (content != null) {
                    Box(Modifier.padding(vertical = 5.dp)) {
                        content.invoke()
                    }
                }
            }

        }
    )
}