package com.boxy.authenticator.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.enter_your_password
import boxy_authenticator.composeapp.generated.resources.password
import com.boxy.authenticator.ui.components.StyledTextField
import org.jetbrains.compose.resources.stringResource

@Composable
fun RequestPasswordDialog(
    title: String,
    body: String? = null,
    label: String = stringResource(Res.string.password),
    placeholder: String = stringResource(Res.string.enter_your_password),
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
) {
    var password by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }

    BoxyDialog(
        dialogTitle = title,
        onDismissRequest = { onDismissRequest() },
        onConfirmation = { onConfirmation(password) }
    ) {
        Column {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            body?.let {
                Text(text = body)
                Spacer(Modifier.height(20.dp))
            }

            StyledTextField(
                value = password,
                onValueChange = { password = it },
                label = label,
                placeholder = placeholder,
                isPasswordField = true,
                modifier = Modifier.focusRequester(focusRequester),
            )
        }
    }
}