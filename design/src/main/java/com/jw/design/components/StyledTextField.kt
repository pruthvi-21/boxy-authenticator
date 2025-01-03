package com.jw.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String = "",
    errorMessage: String? = null,
    isPasswordField: Boolean = false,
    hidePasswordVisibilityEye: Boolean = !isPasswordField,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    readOnly: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    containerModifier: Modifier = Modifier,
    modifier: Modifier = Modifier,
) {
    val hasError = !errorMessage.isNullOrEmpty()

    val showPassword = remember { mutableStateOf(false) }

    Column(modifier = containerModifier) {
        if (!label.isNullOrEmpty()) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(vertical = 2.dp, horizontal = 15.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            placeholder = { Text(placeholder) },
            isError = hasError,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedIndicatorColor = Color.Transparent,
                errorContainerColor = MaterialTheme.colorScheme.errorContainer,
                errorIndicatorColor = Color.Transparent,
                errorTextColor = MaterialTheme.colorScheme.onErrorContainer,
                disabledIndicatorColor = Color.Transparent,
            ),
            readOnly = readOnly,
            leadingIcon = leadingIcon,
            trailingIcon = if (isPasswordField && !hidePasswordVisibilityEye) {
                {
                    val image = if (showPassword.value) Icons.Filled.VisibilityOff
                    else Icons.Filled.Visibility

                    val description = if (showPassword.value) "Hide password"
                    else "Show password"

                    IconButton(onClick = { showPassword.value = !showPassword.value }) {
                        Icon(
                            imageVector = image,
                            contentDescription = description
                        )
                    }
                }
            } else trailingIcon,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = if (isPasswordField && !showPassword.value) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            singleLine = true,
            supportingText = if (hasError) {
                { Text(errorMessage!!) }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)
        )
    }
}