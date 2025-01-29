package com.boxy.authenticator.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.app_name
import boxy_authenticator.composeapp.generated.resources.enter_your_password
import boxy_authenticator.composeapp.generated.resources.ic_app_logo
import boxy_authenticator.composeapp.generated.resources.title_settings
import boxy_authenticator.composeapp.generated.resources.unlock
import boxy_authenticator.composeapp.generated.resources.unlock_vault
import boxy_authenticator.composeapp.generated.resources.unlock_vault_message
import boxy_authenticator.composeapp.generated.resources.use_biometrics
import com.boxy.authenticator.navigation.components.AuthenticationScreenComponent
import com.boxy.authenticator.ui.components.StyledTextField
import com.boxy.authenticator.ui.components.TokkyButton
import com.boxy.authenticator.ui.components.TokkyTextButton
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.utils.BuildUtils
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(component: AuthenticationScreenComponent) {
    val authViewModel = component.authenticationViewModel
    val focusRequester = remember { FocusRequester() }

    val isBiometricUnlockEnabled = authViewModel.isBiometricUnlockEnabled()

    LaunchedEffect(Unit) {
        if (isBiometricUnlockEnabled) {
            authViewModel.promptForBiometrics {
                if (it) component.navigateToHome()
            }
        } else {
            delay(10)
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            Toolbar(
                title = "",
                actions = {
                    IconButton(onClick = { component.navigateToSettings() }) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = stringResource(Res.string.title_settings)
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_app_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(60.dp)
                        .clip(MaterialTheme.shapes.small)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = stringResource(Res.string.unlock_vault),
                    style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = stringResource(Res.string.unlock_vault_message),
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 10.dp)
                )

                Spacer(Modifier.height(30.dp))

                StyledTextField(
                    value = authViewModel.password.value,
                    onValueChange = { authViewModel.updatePassword(it) },
                    placeholder = stringResource(Res.string.enter_your_password),
                    isPasswordField = true,
                    errorMessage = authViewModel.passwordError.value,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (authViewModel.showPinPad.value) KeyboardType.Number
                        else KeyboardType.Text,
                    ),
                    modifier = Modifier.focusRequester(focusRequester)
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isBiometricUnlockEnabled) {
                        TokkyTextButton(
                            onClick = {
                                authViewModel.promptForBiometrics {
                                    if (it) component.navigateToHome()
                                }
                            },
                        ) {
                            Text(stringResource(Res.string.use_biometrics))
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    TokkyButton(
                        onClick = {
                            authViewModel.verifyPassword {
                                if (!it) return@verifyPassword
                                component.navigateToHome()
                            }
                        },
                        enabled = authViewModel.password.value.isNotEmpty(),
                    ) {
                        Text(stringResource(Res.string.unlock))
                    }
                }

                Spacer(Modifier.weight(1f))

                Text(
                    "${stringResource(Res.string.app_name)} ${BuildUtils.getAppVersionName()}",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}
