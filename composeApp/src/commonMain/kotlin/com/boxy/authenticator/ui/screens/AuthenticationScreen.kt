package com.boxy.authenticator.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.app_name
import boxy_authenticator.composeapp.generated.resources.biometric_prompt_title
import boxy_authenticator.composeapp.generated.resources.cancel
import boxy_authenticator.composeapp.generated.resources.enter_your_password
import boxy_authenticator.composeapp.generated.resources.ic_app_logo
import boxy_authenticator.composeapp.generated.resources.title_settings
import boxy_authenticator.composeapp.generated.resources.to_unlock
import boxy_authenticator.composeapp.generated.resources.unlock
import boxy_authenticator.composeapp.generated.resources.unlock_vault
import boxy_authenticator.composeapp.generated.resources.unlock_vault_message
import boxy_authenticator.composeapp.generated.resources.use_biometrics
import com.boxy.authenticator.core.BiometricsHelper
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.components.design.BoxyButton
import com.boxy.authenticator.ui.components.design.BoxyScaffold
import com.boxy.authenticator.ui.components.design.BoxyTextButton
import com.boxy.authenticator.ui.components.design.BoxyTextField
import com.boxy.authenticator.ui.state.AuthenticationUiState
import com.boxy.authenticator.utils.BuildUtils
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(
    uiState: AuthenticationUiState,
    isPinPadVisible: Boolean,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    updatePinPadVisibility: () -> Unit,
    onAuthSuccess: () -> Unit,
    navigateToSettings: (hideSensitiveSettings: Boolean) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val biometricsHelper: BiometricsHelper = koinInject()

    val scope = rememberCoroutineScope()

    val isBiometricUnlockEnabled = biometricsHelper.isBiometricAvailable()

    fun promptForBiometrics() {
        scope.launch {
            val isSuccess = biometricsHelper.promptForBiometrics(
                title = getString(Res.string.biometric_prompt_title),
                reason = getString(Res.string.to_unlock),
                failureButtonText = getString(Res.string.cancel),
            )

            if (isSuccess) onAuthSuccess()
        }
    }

    LaunchedEffect(Unit) {
        if (isBiometricUnlockEnabled) promptForBiometrics()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            updatePinPadVisibility()
        }
    }

    BoxyScaffold(
        topBar = {
            Toolbar(
                title = "",
                actions = {
                    IconButton(onClick = { navigateToSettings(true) }) {
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
                    .verticalScroll(rememberScrollState()),
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

                LaunchedEffect(Unit) {
                    if (!isBiometricUnlockEnabled) {
                        focusRequester.requestFocus()
                    }
                }
                BoxyTextField(
                    value = uiState.password,
                    onValueChange = { onPasswordChange(it) },
                    placeholder = stringResource(Res.string.enter_your_password),
                    isPasswordField = true,
                    errorMessage = uiState.passwordError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (isPinPadVisible) KeyboardType.Number
                        else KeyboardType.Text,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onSubmit() }
                    ),
                    modifier = Modifier.focusRequester(focusRequester)
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isBiometricUnlockEnabled) {
                        BoxyTextButton(
                            onClick = { promptForBiometrics() },
                        ) {
                            Text(stringResource(Res.string.use_biometrics))
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    BoxyButton(
                        onClick = { onSubmit() },
                        enabled = uiState.password.isNotEmpty() && !uiState.isVerifyingPassword,
                    ) {
                        if (uiState.isVerifyingPassword) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp)
                            )
                        } else {
                            Text(stringResource(Res.string.unlock))
                        }
                    }
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
