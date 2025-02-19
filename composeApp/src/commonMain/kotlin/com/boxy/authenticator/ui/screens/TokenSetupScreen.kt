package com.boxy.authenticator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.account_exists_dialog_message
import boxy_authenticator.composeapp.generated.resources.account_exists_dialog_title
import boxy_authenticator.composeapp.generated.resources.cancel
import boxy_authenticator.composeapp.generated.resources.dialog_message_delete_token
import boxy_authenticator.composeapp.generated.resources.hint_counter
import boxy_authenticator.composeapp.generated.resources.hint_issuer
import boxy_authenticator.composeapp.generated.resources.hint_label
import boxy_authenticator.composeapp.generated.resources.hint_period
import boxy_authenticator.composeapp.generated.resources.hint_secret_key
import boxy_authenticator.composeapp.generated.resources.label_add_account
import boxy_authenticator.composeapp.generated.resources.label_advanced_options
import boxy_authenticator.composeapp.generated.resources.label_algorithm
import boxy_authenticator.composeapp.generated.resources.label_counter
import boxy_authenticator.composeapp.generated.resources.label_digits
import boxy_authenticator.composeapp.generated.resources.label_issuer
import boxy_authenticator.composeapp.generated.resources.label_label
import boxy_authenticator.composeapp.generated.resources.label_period
import boxy_authenticator.composeapp.generated.resources.label_secret_key
import boxy_authenticator.composeapp.generated.resources.label_update_account
import boxy_authenticator.composeapp.generated.resources.message_unsaved_changes
import boxy_authenticator.composeapp.generated.resources.no
import boxy_authenticator.composeapp.generated.resources.remove
import boxy_authenticator.composeapp.generated.resources.remove_account
import boxy_authenticator.composeapp.generated.resources.rename
import boxy_authenticator.composeapp.generated.resources.replace
import boxy_authenticator.composeapp.generated.resources.title_enter_account_details
import boxy_authenticator.composeapp.generated.resources.title_update_account_details
import boxy_authenticator.composeapp.generated.resources.type
import boxy_authenticator.composeapp.generated.resources.warning
import boxy_authenticator.composeapp.generated.resources.yes
import com.boxy.authenticator.core.TokenEntryParser
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.models.enums.OTPType
import com.boxy.authenticator.domain.models.enums.TokenSetupMode
import com.boxy.authenticator.domain.models.form.TokenFormEvent
import com.boxy.authenticator.domain.models.otp.OtpInfo
import com.boxy.authenticator.domain.models.otp.TotpInfo.Companion.DEFAULT_PERIOD
import com.boxy.authenticator.navigation.LocalNavController
import com.boxy.authenticator.ui.components.design.BoxyScaffold
import com.boxy.authenticator.ui.components.design.BoxyDropdownTextField
import com.boxy.authenticator.ui.components.design.BoxyTextField
import com.boxy.authenticator.ui.components.ThumbnailController
import com.boxy.authenticator.ui.components.design.BoxyButton
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.components.dialogs.BoxyDialog
import com.boxy.authenticator.ui.util.SystemBackHandler
import com.boxy.authenticator.ui.viewmodels.TokenSetupViewModel
import com.boxy.authenticator.utils.getInitials
import com.boxy.authenticator.utils.name
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun TokenSetupFromUrlScreen(authUrl: String) {
    val tokenSetupViewModel: TokenSetupViewModel = koinViewModel()
    var token by remember { mutableStateOf<TokenEntry?>(null) }

    LaunchedEffect(authUrl) {
        token = try {
            TokenEntryParser.buildFromUrl(authUrl)
        } catch (e: Exception) {
            null
        }
    }

    if (token != null) {
        TokenSetupScreen(
            token = token!!,
            tokenSetupMode = TokenSetupMode.URL,
            tokenSetupViewModel = tokenSetupViewModel,
        )
    }
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun EditTokenScreen(tokenId: String) {
    val tokenSetupViewModel: TokenSetupViewModel = koinViewModel()
    val token = remember { tokenSetupViewModel.getTokenFromId(tokenId) }

    if (token != null) {
        TokenSetupScreen(
            token = token,
            tokenSetupMode = TokenSetupMode.UPDATE,
            tokenSetupViewModel = tokenSetupViewModel,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun TokenSetupScreen(
    token: TokenEntry? = null,
    tokenSetupMode: TokenSetupMode = TokenSetupMode.NEW,
    tokenSetupViewModel: TokenSetupViewModel = koinViewModel(),
) {

    val navController = LocalNavController.current
    val localFocus = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var state = tokenSetupViewModel.uiState.value

    LaunchedEffect(token, tokenSetupMode) {
        tokenSetupViewModel.setStateFromToken(token, tokenSetupMode)
        state = tokenSetupViewModel.uiState.value
    }

    SystemBackHandler {
        if (tokenSetupViewModel.isFormUpdated()) {
            tokenSetupViewModel.showBackPressDialog.value = true
        } else {
            navController.navigateUp()
        }
    }

    BoxyScaffold(
        topBar = {
            val title =
                if (tokenSetupMode == TokenSetupMode.UPDATE) stringResource(Res.string.title_update_account_details)
                else stringResource(Res.string.title_enter_account_details)

            Toolbar(
                title = title,
                showDefaultNavigationIcon = true,
                onNavigationIconClick = {
                    if (tokenSetupViewModel.isFormUpdated()) {
                        tokenSetupViewModel.showBackPressDialog.value = true
                    } else {
                        navController.navigateUp()
                    }
                },
                actions = {
                    if (tokenSetupMode == TokenSetupMode.UPDATE) {
                        IconButton(onClick = {
                            tokenSetupViewModel.showDeleteTokenDialog.value = true
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(Res.string.remove),
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                },
            )
        }
    ) { safePadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(safePadding)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            localFocus.clearFocus()
                            keyboardController?.hide()
                        }
                    )
                }
        ) {

            if (tokenSetupViewModel.showBackPressDialog.value) {
                BoxyDialog(
                    dialogTitle = stringResource(Res.string.warning),
                    dialogBody = stringResource(Res.string.message_unsaved_changes),
                    dismissText = stringResource(Res.string.no),
                    confirmText = stringResource(Res.string.yes),
                    onDismissRequest = { tokenSetupViewModel.showBackPressDialog.value = false },
                    onConfirmation = {
                        navController.navigateUp()
                        tokenSetupViewModel.showBackPressDialog.value = false
                    }
                )
            }

            if (tokenSetupViewModel.showDeleteTokenDialog.value) {
                BoxyDialog(
                    dialogTitle = stringResource(Res.string.remove_account),
                    dialogBody = stringResource(
                        Res.string.dialog_message_delete_token,
                        state.issuer,
                        state.label
                    ),
                    dismissText = stringResource(Res.string.cancel),
                    confirmText = stringResource(Res.string.remove),
                    isDestructive = true,
                    onDismissRequest = {
                        tokenSetupViewModel.showDeleteTokenDialog.value = false
                    },
                    onConfirmation = {
                        tokenSetupViewModel.deleteToken(
                            tokenId = token!!.id,
                            onComplete = {
                                tokenSetupViewModel.showDeleteTokenDialog.value = false
                                navController.navigateUp()
                            }
                        )
                    }
                )
            }

            if (tokenSetupViewModel.showDuplicateTokenDialog.value.show) {
                val args = tokenSetupViewModel.showDuplicateTokenDialog.value
                BoxyDialog(
                    dialogTitle = stringResource(Res.string.account_exists_dialog_title),
                    dialogBody = stringResource(
                        Res.string.account_exists_dialog_message,
                        args.token!!.name
                    ),
                    dismissText = stringResource(Res.string.rename),
                    confirmText = stringResource(Res.string.replace),
                    onDismissRequest = {
                        tokenSetupViewModel.showDuplicateTokenDialog.value =
                            TokenSetupViewModel.DuplicateTokenDialogArgs(false)
                    },
                    onConfirmation = {
                        tokenSetupViewModel.replaceExistingToken(
                            existingToken = args.existingToken!!,
                            token = args.token
                        )
                        navController.navigateUp()
                        tokenSetupViewModel.showDuplicateTokenDialog.value =
                            TokenSetupViewModel.DuplicateTokenDialogArgs(false)
                    }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 40.dp)
            ) {
                ThumbnailController(
                    text = state.issuer.getInitials(),
                    thumbnail = state.thumbnail,
                    onThumbnailChanged = {
                        keyboardController?.hide()
                        tokenSetupViewModel.onEvent(TokenFormEvent.ThumbnailChanged(it))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Issuer Field
                BoxyTextField(
                    value = state.issuer,
                    onValueChange = { tokenSetupViewModel.onEvent(TokenFormEvent.IssuerChanged(it)) },
                    label = stringResource(Res.string.label_issuer),
                    placeholder = stringResource(Res.string.hint_issuer),
                    errorMessage = state.validationErrors["issuer"],
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { localFocus.moveFocus(FocusDirection.Down) }
                    ),
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Label Field
                BoxyTextField(
                    value = state.label,
                    onValueChange = { tokenSetupViewModel.onEvent(TokenFormEvent.LabelChanged(it)) },
                    label = stringResource(Res.string.label_label),
                    placeholder = stringResource(Res.string.hint_label),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { localFocus.moveFocus(FocusDirection.Down) }
                    ),
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Secret Key Field
                BoxyTextField(
                    value = state.secretKey,
                    onValueChange = {
                        tokenSetupViewModel.onEvent(TokenFormEvent.SecretKeyChanged(it))
                    },
                    label = stringResource(Res.string.label_secret_key),
                    placeholder = stringResource(Res.string.hint_secret_key),
                    isPasswordField = true,
                    errorMessage = state.validationErrors["secretKey"],
                    enabled = !tokenSetupViewModel.lockSensitiveFields,
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormAdvancedOptions(
                    onShowAdvancedOptions = {
                        keyboardController?.hide()
                        tokenSetupViewModel.onEvent(
                            TokenFormEvent.EnableAdvancedOptionsChanged(it)
                        )
                    },
                    tokenSetupViewModel = tokenSetupViewModel,
                )
            }

            val buttonText =
                if (tokenSetupMode == TokenSetupMode.UPDATE) stringResource(Res.string.label_update_account)
                else stringResource(Res.string.label_add_account)

            BoxyButton(
                onClick = {
                    keyboardController?.hide()

                    tokenSetupViewModel.onEvent(
                        TokenFormEvent.Submit(
                            onComplete = {
                                navController.navigateUp()
                            },
                            onDuplicate = { token, existingToken ->
                                tokenSetupViewModel.showDuplicateTokenDialog.value =
                                    TokenSetupViewModel.DuplicateTokenDialogArgs(
                                        true, token, existingToken
                                    )
                            }
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .heightIn(min = 46.dp),
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Composable
fun FormAdvancedOptions(
    onShowAdvancedOptions: (Boolean) -> Unit,
    tokenSetupViewModel: TokenSetupViewModel,
) {
    val state = tokenSetupViewModel.uiState.value

    Column(Modifier.fillMaxWidth()) {
        if (!state.enableAdvancedOptions) {
            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            onShowAdvancedOptions(true)
                        }
                        .padding(10.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.label_advanced_options),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(-90f),
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = state.enableAdvancedOptions,
            enter = expandVertically(animationSpec = tween(200)),
            exit = shrinkVertically(animationSpec = tween(200))
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    BoxyDropdownTextField(
                        label = stringResource(Res.string.type),
                        value = state.type.name,
                        values = OTPType.entries.map { it.name },
                        defaultValue = OTPType.TOTP.name,
                        onItemSelected = {
                            tokenSetupViewModel.onEvent(
                                TokenFormEvent.TypeChanged(OTPType.valueOf(it))
                            )
                        },
                        enabled = !tokenSetupViewModel.lockSensitiveFields,
                        modifier = Modifier.weight(1f),
                    )

                    if (state.isAlgorithmFieldVisible) {
                        BoxyDropdownTextField(
                            label = stringResource(Res.string.label_algorithm),
                            value = state.algorithm,
                            values = listOf("SHA1", "SHA256", "SHA512"),
                            defaultValue = OtpInfo.DEFAULT_ALGORITHM,
                            onItemSelected = {
                                tokenSetupViewModel.onEvent(TokenFormEvent.AlgorithmChanged(it))
                            },
                            enabled = !tokenSetupViewModel.lockSensitiveFields,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    if (state.isDigitsFieldVisible) {
                        BoxyDropdownTextField(
                            label = stringResource(Res.string.label_digits),
                            value = state.digits,
                            values = listOf("4", "5", "6", "7", "8", "9", "10"),
                            defaultValue = "${OtpInfo.DEFAULT_DIGITS}",
                            onItemSelected = {
                                tokenSetupViewModel.onEvent(TokenFormEvent.DigitsChanged(it))
                            },
                            enabled = !tokenSetupViewModel.lockSensitiveFields,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    if (state.isPeriodFieldVisible) {
                        BoxyTextField(
                            value = state.period,
                            onValueChange = {
                                tokenSetupViewModel.onEvent(TokenFormEvent.PeriodChanged(it))
                            },
                            label = stringResource(Res.string.label_period),
                            placeholder = stringResource(Res.string.hint_period, "$DEFAULT_PERIOD"),
                            errorMessage = state.validationErrors["period"],
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                            ),
                            containerModifier = Modifier.weight(1f),
                            enabled = !tokenSetupViewModel.lockSensitiveFields,
                        )
                    }

                    if (state.isCounterFieldVisible) {
                        BoxyTextField(
                            value = state.counter,
                            onValueChange = {
                                tokenSetupViewModel.onEvent(TokenFormEvent.CounterChanged(it))
                            },
                            label = stringResource(Res.string.label_counter),
                            placeholder = stringResource(Res.string.hint_counter),
                            errorMessage = state.validationErrors["counter"],
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                            ),
                            containerModifier = Modifier.weight(1f),
                            enabled = !tokenSetupViewModel.lockSensitiveFields,
                        )
                    }
                }
            }
        }
    }
}
