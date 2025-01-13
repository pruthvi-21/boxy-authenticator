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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.boxy.authenticator.data.models.otp.OtpInfo
import com.boxy.authenticator.data.models.otp.TotpInfo.Companion.DEFAULT_PERIOD
import com.boxy.authenticator.domain.models.TokenFormEvent
import com.boxy.authenticator.navigation.TokenSetupScreenComponent
import com.boxy.authenticator.ui.components.DropdownTextField
import com.boxy.authenticator.ui.components.StyledTextField
import com.boxy.authenticator.ui.components.ThumbnailController
import com.boxy.authenticator.ui.components.TokkyButton
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.components.dialogs.TokenDeleteDialog
import com.boxy.authenticator.ui.components.dialogs.TokkyDialog
import com.boxy.authenticator.ui.viewmodels.TokenSetupViewModel
import com.boxy.authenticator.utils.OTPType
import com.boxy.authenticator.utils.TokenSetupMode
import com.boxy.authenticator.utils.getInitials

@Composable
fun TokenSetupScreen(component: TokenSetupScreenComponent) {
    val tokenId = component.tokenId
    val authUrl = component.authUrl
    val tokenSetupViewModel: TokenSetupViewModel = component.viewModel

    val localFocus = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
//    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    var state = tokenSetupViewModel.uiState.value
    val tokenSetupMode = tokenSetupViewModel.tokenSetupMode

    LaunchedEffect(tokenId, authUrl, tokenSetupMode) {
        tokenId?.let {
            tokenSetupViewModel.setInitialStateFromTokenWithId(tokenId)
            state = tokenSetupViewModel.uiState.value
        }

        authUrl?.let {
            tokenSetupViewModel.setInitialStateFromUrl(authUrl)
            state = tokenSetupViewModel.uiState.value
        }
    }

//    BackHandler(enabled = tokenSetupViewModel.isFormUpdated()) {
//        tokenSetupViewModel.showBackPressDialog.value = true
//    }

    Scaffold(
        topBar = {
            val title =
                if (tokenSetupMode == TokenSetupMode.UPDATE) "Update account details"
                else "Enter account details"

            Toolbar(
                title = title,
                showDefaultNavigationIcon = true,
                onNavigationIconClick = { component.navigateUp() },
                actions = {
                    if (tokenSetupMode == TokenSetupMode.UPDATE) {
                        IconButton(onClick = {
                            tokenSetupViewModel.showDeleteTokenDialog.value = true
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
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
                TokkyDialog(
                    dialogBody = "Going back will clear all the fields. Are you sure to go back?",
                    confirmText = "Go back",
                    onDismissRequest = { tokenSetupViewModel.showBackPressDialog.value = false },
                    onConfirmation = {
                        component.navigateUp()
                        tokenSetupViewModel.showBackPressDialog.value = false
                    }
                )
            }

            if (tokenSetupViewModel.showDeleteTokenDialog.value) {
                TokenDeleteDialog(
                    issuer = state.issuer,
                    label = state.label,
                    onDismiss = {
                        tokenSetupViewModel.showDeleteTokenDialog.value = false
                    },
                    onConfirm = {
                        tokenSetupViewModel.deleteToken(
                            tokenId = tokenId!!,
                            onComplete = {
                                tokenSetupViewModel.showDeleteTokenDialog.value = false
                                component.navigateUp()
                            }
                        )
                    }
                )
            }

            if (tokenSetupViewModel.showDuplicateTokenDialog.value.show) {
                val args = tokenSetupViewModel.showDuplicateTokenDialog.value
                TokkyDialog(
                    dialogTitle = "Account already exists",
                    dialogBody = "You already have an account with name ${args.token!!.name}",
                    confirmText = "Replace",
                    dismissText = "Rename",
                    onDismissRequest = {
                        tokenSetupViewModel.showDuplicateTokenDialog.value =
                            TokenSetupViewModel.DuplicateTokenDialogArgs(false)
                    },
                    onConfirmation = {
                        tokenSetupViewModel.replaceExistingToken(
                            existingToken = args.existingToken!!,
                            token = args.token
                        )
                        component.navigateUp()
                        tokenSetupViewModel.showDuplicateTokenDialog.value =
                            TokenSetupViewModel.DuplicateTokenDialogArgs(false)
                    },
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
                    colorHex = state.thumbnailColor,
                    onColorChanged = { color ->
                        keyboardController?.hide()
                        tokenSetupViewModel.onEvent(TokenFormEvent.ThumbnailColorChanged(color))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Issuer Field
                StyledTextField(
                    value = state.issuer,
                    onValueChange = { tokenSetupViewModel.onEvent(TokenFormEvent.IssuerChanged(it)) },
                    label = "Issuer",
                    placeholder = "Name of issuer",
                    errorMessage = state.validationErrors["issuer"],
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { localFocus.moveFocus(FocusDirection.Down) }
                    ),
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Label Field
                StyledTextField(
                    value = state.label,
                    onValueChange = { tokenSetupViewModel.onEvent(TokenFormEvent.LabelChanged(it)) },
                    label = "Label",
                    placeholder = "Email or username",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { localFocus.moveFocus(FocusDirection.Down) }
                    ),
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (tokenSetupMode == TokenSetupMode.NEW) {
                    // Secret Key Field
                    StyledTextField(
                        value = state.secretKey,
                        onValueChange = {
                            tokenSetupViewModel.onEvent(TokenFormEvent.SecretKeyChanged(it))
                        },
                        label = "Secret Key",
                        placeholder = "Secret Key",
                        isPasswordField = true,
                        errorMessage = state.validationErrors["secretKey"],
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
            }

            val buttonText =
                if (tokenSetupMode == TokenSetupMode.UPDATE) "Update account"
                else "Add account"

            TokkyButton(
                onClick = {
                    keyboardController?.hide()

                    tokenSetupViewModel.onEvent(
                        TokenFormEvent.Submit(
                            onComplete = {
                                component.navigateUp()
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
                        text = "Advanced options",
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
                    DropdownTextField(
                        label = "Type",
                        value = state.type.name,
                        values = OTPType.entries.map { it.name },
                        defaultValue = OTPType.TOTP.name,
                        onItemSelected = {
                            tokenSetupViewModel.onEvent(
                                TokenFormEvent.TypeChanged(
                                    OTPType.valueOf(
                                        it
                                    )
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                    )

                    if (state.isAlgorithmFieldVisible) {
                        DropdownTextField(
                            label = "Algorithm",
                            value = state.algorithm,
                            values = listOf("SHA1", "SHA256", "SHA512"),
                            defaultValue = OtpInfo.DEFAULT_ALGORITHM,
                            onItemSelected = {
                                tokenSetupViewModel.onEvent(TokenFormEvent.AlgorithmChanged(it))
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    if (state.isDigitsFieldVisible) {
                        DropdownTextField(
                            label = "Digits",
                            value = state.digits,
                            values = listOf("4", "5", "6", "7", "8", "9", "10"),
                            defaultValue = "${OtpInfo.DEFAULT_DIGITS}",
                            onItemSelected = {
                                tokenSetupViewModel.onEvent(TokenFormEvent.DigitsChanged(it))
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }

                    if (state.isPeriodFieldVisible) {
                        StyledTextField(
                            value = state.period,
                            onValueChange = {
                                tokenSetupViewModel.onEvent(TokenFormEvent.PeriodChanged(it))
                            },
                            label = "Period",
                            placeholder = "$DEFAULT_PERIOD (Default)",
                            errorMessage = state.validationErrors["period"],
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                            ),
                            containerModifier = Modifier.weight(1f),
                        )
                    }

                    if (state.isCounterFieldVisible) {
                        StyledTextField(
                            value = state.counter,
                            onValueChange = {
                                tokenSetupViewModel.onEvent(TokenFormEvent.CounterChanged(it))
                            },
                            label = "Counter",
                            placeholder = "Counter",
                            errorMessage = state.validationErrors["counter"],
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                            ),
                            containerModifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}
