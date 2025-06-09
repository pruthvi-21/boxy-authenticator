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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.models.enums.OTPType
import com.boxy.authenticator.domain.models.enums.TokenSetupMode
import com.boxy.authenticator.domain.models.form.TokenFormEvent
import com.boxy.authenticator.ui.state.TokenSetupUiState
import com.boxy.authenticator.domain.models.otp.OtpInfo
import com.boxy.authenticator.domain.models.otp.TotpInfo.Companion.DEFAULT_PERIOD
import com.boxy.authenticator.ui.components.ThumbnailController
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.components.design.BoxyButton
import com.boxy.authenticator.ui.components.design.BoxyDropdownTextField
import com.boxy.authenticator.ui.components.design.BoxyScaffold
import com.boxy.authenticator.ui.components.design.BoxyTextField
import com.boxy.authenticator.ui.components.dialogs.BoxyDialog
import com.boxy.authenticator.ui.util.SystemBackHandler
import com.boxy.authenticator.ui.viewmodels.TokenSetupViewModel
import com.boxy.authenticator.utils.getInitials
import com.boxy.authenticator.utils.name
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun TokenSetupScreen(
    viewModel: TokenSetupViewModel,
    tokenId: String?,
    authUrl: String? = null,
    setupMode: TokenSetupMode,
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(authUrl) {
        if (authUrl != null) {
            viewModel.setStateFromAuthUrl(authUrl)
        }
    }

    LaunchedEffect(tokenId) {
        if (tokenId != null && setupMode == TokenSetupMode.UPDATE) {
            viewModel.setStateFromToken(viewModel.getTokenFromId(tokenId), TokenSetupMode.UPDATE)
        }
    }

    TokenSetupScreen(
        uiState = uiState,
        lockSensitiveFields = viewModel.lockSensitiveFields,
        onFormEvent = viewModel::onEvent,
        showBackPressDialog = viewModel::showBackPressDialog,
        showDeleteTokenDialog = viewModel::showDeleteTokenDialog,
        showDuplicateTokenDialog = viewModel::showDuplicateTokenDialog,
        deleteToken = viewModel::deleteToken,
        replaceExistingToken = viewModel::replaceExistingToken,
        onBackPress = {
            if (viewModel.isFormUpdated()) {
                viewModel.showBackPressDialog(true)
            } else {
                navController.navigateUp()
            }
        },
        navigateUp = navController::navigateUp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TokenSetupScreen(
    uiState: TokenSetupUiState,
    lockSensitiveFields: Boolean,
    onFormEvent: (TokenFormEvent) -> Unit,
    showBackPressDialog: (Boolean) -> Unit,
    showDeleteTokenDialog: (Boolean) -> Unit,
    showDuplicateTokenDialog: (TokenSetupViewModel.DuplicateTokenDialogArgs) -> Unit,
    deleteToken: () -> Unit,
    replaceExistingToken: (existingToken: TokenEntry, token: TokenEntry) -> Unit,
    onBackPress: () -> Unit,
    navigateUp: () -> Unit,
) {
    val localFocus = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val scope = rememberCoroutineScope()

    SystemBackHandler { onBackPress() }

    BoxyScaffold(
        topBar = {
            val title =
                if (uiState.tokenSetupMode == TokenSetupMode.UPDATE) stringResource(Res.string.title_update_account_details)
                else stringResource(Res.string.title_enter_account_details)

            Toolbar(
                title = title,
                showDefaultNavigationIcon = true,
                onNavigationIconClick = onBackPress,
                actions = {
                    if (uiState.tokenSetupMode == TokenSetupMode.UPDATE) {
                        IconButton(onClick = { showDeleteTokenDialog(true) }) {
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

            if (uiState.showBackPressDialog) {
                BoxyDialog(
                    dialogTitle = stringResource(Res.string.warning),
                    dialogBody = stringResource(Res.string.message_unsaved_changes),
                    dismissText = stringResource(Res.string.no),
                    confirmText = stringResource(Res.string.yes),
                    onDismissRequest = { showBackPressDialog(false) },
                    onConfirmation = {
                        navigateUp()
                        showBackPressDialog(false)
                    }
                )
            }

            if (uiState.showDeleteTokenDialog) {
                BoxyDialog(
                    dialogTitle = stringResource(Res.string.remove_account),
                    dialogBody = stringResource(
                        Res.string.dialog_message_delete_token,
                        uiState.issuer,
                        uiState.label
                    ),
                    dismissText = stringResource(Res.string.cancel),
                    confirmText = stringResource(Res.string.remove),
                    isDestructive = true,
                    onDismissRequest = {
                        showDeleteTokenDialog(false)
                    },
                    onConfirmation = {
                        scope.launch {
                            deleteToken()
                            showDeleteTokenDialog(false)
                            navigateUp()
                        }
                    }
                )
            }

            if (uiState.showDuplicateTokenDialog.show) {
                val args = uiState.showDuplicateTokenDialog
                BoxyDialog(
                    dialogTitle = stringResource(Res.string.account_exists_dialog_title),
                    dialogBody = stringResource(
                        Res.string.account_exists_dialog_message,
                        args.token!!.name
                    ),
                    dismissText = stringResource(Res.string.rename),
                    confirmText = stringResource(Res.string.replace),
                    onDismissRequest = {
                        showDuplicateTokenDialog(TokenSetupViewModel.DuplicateTokenDialogArgs(false))
                    },
                    onConfirmation = {
                        replaceExistingToken(args.existingToken!!, args.token)
                        navigateUp()
                        showDuplicateTokenDialog(TokenSetupViewModel.DuplicateTokenDialogArgs(false))
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
                    text = uiState.issuer.getInitials(),
                    thumbnail = uiState.thumbnail,
                    onThumbnailChanged = {
                        keyboardController?.hide()
                        onFormEvent(TokenFormEvent.ThumbnailChanged(it))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Issuer Field
                BoxyTextField(
                    value = uiState.issuer,
                    onValueChange = { onFormEvent(TokenFormEvent.IssuerChanged(it)) },
                    label = stringResource(Res.string.label_issuer),
                    placeholder = stringResource(Res.string.hint_issuer),
                    errorMessage = uiState.validationErrors["issuer"],
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { localFocus.moveFocus(FocusDirection.Down) }
                    ),
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Label Field
                BoxyTextField(
                    value = uiState.label,
                    onValueChange = { onFormEvent(TokenFormEvent.LabelChanged(it)) },
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
                    value = uiState.secretKey,
                    onValueChange = {
                        onFormEvent(TokenFormEvent.SecretKeyChanged(it))
                    },
                    label = stringResource(Res.string.label_secret_key),
                    placeholder = stringResource(Res.string.hint_secret_key),
                    isPasswordField = true,
                    errorMessage = uiState.validationErrors["secretKey"],
                    enabled = !lockSensitiveFields,
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormAdvancedOptions(
                    uiState = uiState,
                    onShowAdvancedOptions = {
                        keyboardController?.hide()
                        onFormEvent(TokenFormEvent.EnableAdvancedOptionsChanged(it))
                    },
                    onFormEvent = onFormEvent,
                    lockSensitiveFields = lockSensitiveFields,
                )
            }

            val buttonText =
                if (uiState.tokenSetupMode == TokenSetupMode.UPDATE) stringResource(Res.string.label_update_account)
                else stringResource(Res.string.label_add_account)

            BoxyButton(
                onClick = {
                    keyboardController?.hide()

                    onFormEvent(
                        TokenFormEvent.Submit(
                            onComplete = { navigateUp() },
                            onDuplicate = { token, existingToken ->
                                showDuplicateTokenDialog(
                                    TokenSetupViewModel.DuplicateTokenDialogArgs(
                                        show = true,
                                        token = token,
                                        existingToken = existingToken,
                                    )
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
    uiState: TokenSetupUiState,
    onShowAdvancedOptions: (Boolean) -> Unit,
    onFormEvent: (TokenFormEvent) -> Unit,
    lockSensitiveFields: Boolean,
) {
    Column(Modifier.fillMaxWidth()) {
        if (!uiState.enableAdvancedOptions) {
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
            visible = uiState.enableAdvancedOptions,
            enter = expandVertically(animationSpec = tween(200)),
            exit = shrinkVertically(animationSpec = tween(200))
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    BoxyDropdownTextField(
                        label = stringResource(Res.string.type),
                        value = uiState.type.name,
                        values = OTPType.entries.map { it.name },
                        defaultValue = OTPType.TOTP.name,
                        onItemSelected = {
                            onFormEvent(TokenFormEvent.TypeChanged(OTPType.valueOf(it)))
                        },
                        enabled = !lockSensitiveFields,
                        modifier = Modifier.weight(1f),
                    )

                    if (uiState.isAlgorithmFieldVisible) {
                        BoxyDropdownTextField(
                            label = stringResource(Res.string.label_algorithm),
                            value = uiState.algorithm,
                            values = listOf("SHA1", "SHA256", "SHA512"),
                            defaultValue = OtpInfo.DEFAULT_ALGORITHM,
                            onItemSelected = {
                                onFormEvent(TokenFormEvent.AlgorithmChanged(it))
                            },
                            enabled = !lockSensitiveFields,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    if (uiState.isDigitsFieldVisible) {
                        BoxyDropdownTextField(
                            label = stringResource(Res.string.label_digits),
                            value = uiState.digits,
                            values = listOf("4", "5", "6", "7", "8", "9", "10"),
                            defaultValue = "${OtpInfo.DEFAULT_DIGITS}",
                            onItemSelected = { onFormEvent(TokenFormEvent.DigitsChanged(it)) },
                            enabled = !lockSensitiveFields,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    if (uiState.isPeriodFieldVisible) {
                        BoxyTextField(
                            value = uiState.period,
                            onValueChange = { onFormEvent(TokenFormEvent.PeriodChanged(it)) },
                            label = stringResource(Res.string.label_period),
                            placeholder = stringResource(Res.string.hint_period, "$DEFAULT_PERIOD"),
                            errorMessage = uiState.validationErrors["period"],
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                            ),
                            containerModifier = Modifier.weight(1f),
                            enabled = !lockSensitiveFields,
                        )
                    }

                    if (uiState.isCounterFieldVisible) {
                        BoxyTextField(
                            value = uiState.counter,
                            onValueChange = { onFormEvent(TokenFormEvent.CounterChanged(it)) },
                            label = stringResource(Res.string.label_counter),
                            placeholder = stringResource(Res.string.hint_counter),
                            errorMessage = uiState.validationErrors["counter"],
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                            ),
                            containerModifier = Modifier.weight(1f),
                            enabled = !lockSensitiveFields,
                        )
                    }
                }
            }
        }
    }
}
