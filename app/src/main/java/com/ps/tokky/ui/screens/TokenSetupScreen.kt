package com.ps.tokky.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ps.tokky.R
import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.ui.components.MultiToggleButton
import com.ps.tokky.ui.components.StyledTextField
import com.ps.tokky.ui.components.ThumbnailController
import com.ps.tokky.ui.components.Toolbar
import com.ps.tokky.ui.components.dialogs.TokkyDialog
import com.ps.tokky.ui.viewmodels.TokenFormEvent
import com.ps.tokky.ui.viewmodels.TokenFormState
import com.ps.tokky.ui.viewmodels.TokenFormValidationEvent
import com.ps.tokky.ui.viewmodels.TokenFormViewModel
import com.ps.tokky.ui.viewmodels.TokenSetupMode
import com.ps.tokky.ui.viewmodels.TokensViewModel
import com.ps.tokky.utils.OTPType
import com.ps.tokky.utils.getInitials
import java.util.UUID

private val radiusTiny = 4.dp

@Composable
fun TokenSetupScreen(
    tokenId: String? = null,
    authUrl: String? = null,
    tokensViewModel: TokensViewModel,
    tokenFormViewModel: TokenFormViewModel = hiltViewModel(),
    navController: NavController,
) {
    val context = LocalContext.current
    val localFocus = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    var state = tokenFormViewModel.uiState.value
    val tokenSetupMode = tokenFormViewModel.tokenSetupMode

    LaunchedEffect(tokenId, authUrl, tokenSetupMode) {
        tokenId?.let {
            tokenFormViewModel.setInitialStateFromTokenWithId(tokenId)
            state = tokenFormViewModel.uiState.value
        }

        authUrl?.let {
            tokenFormViewModel.setInitialStateFromUrl(authUrl)
            state = tokenFormViewModel.uiState.value
        }
    }

    LaunchedEffect(context, tokenSetupMode) {
        tokenFormViewModel.validationEvent.collect { event ->
            if (event is TokenFormValidationEvent.Success) {
                handleFormSuccess(event.token, tokensViewModel, tokenFormViewModel, navController)
            }
        }
    }

    BackHandler(enabled = tokenFormViewModel.isFormUpdated()) {
        tokenFormViewModel.showBackPressDialog.value = true
    }

    Scaffold(
        topBar = {
            val title =
                if (tokenSetupMode == TokenSetupMode.UPDATE) stringResource(R.string.title_update_account_details)
                else stringResource(R.string.title_enter_account_details)

            Toolbar(
                title = title,
                showDefaultNavigationIcon = true,
                onNavigationIconClick = { backPressedDispatcher?.onBackPressed() },
                actions = {
                    if (tokenSetupMode == TokenSetupMode.UPDATE) {
                        IconButton(onClick = {
                            tokenFormViewModel.showDeleteTokenDialog.value = true
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

            if (tokenFormViewModel.showBackPressDialog.value) {
                TokkyDialog(
                    dialogBody = stringResource(R.string.message_unsaved_changes),
                    confirmText = stringResource(R.string.go_back),
                    onDismissRequest = { tokenFormViewModel.showBackPressDialog.value = false },
                    onConfirmation = {
                        navController.navigateUp()
                        tokenFormViewModel.showBackPressDialog.value = false
                    }
                )
            }

            if (tokenFormViewModel.showDeleteTokenDialog.value) {
                TokenDeleteDialog(
                    tokenId = tokenId!!,
                    state = state,
                    tokensViewModel = tokensViewModel,
                    tokenFormViewModel = tokenFormViewModel,
                    navController = navController
                )
            }

            if (tokenFormViewModel.showDuplicateTokenDialog.value.show) {
                val args = tokenFormViewModel.showDuplicateTokenDialog.value
                TokkyDialog(
                    dialogTitle = stringResource(R.string.account_exists_dialog_title),
                    dialogBody = stringResource(
                        R.string.account_exists_dialog_message,
                        args.token!!.name
                    ),
                    confirmText = stringResource(R.string.replace),
                    dismissText = stringResource(R.string.rename),
                    onDismissRequest = {
                        tokenFormViewModel.showDuplicateTokenDialog.value =
                            TokenFormViewModel.DuplicateTokenDialogArgs(false)
                    },
                    onConfirmation = {
                        tokensViewModel.replaceExistingToken(
                            existingToken = args.existingToken!!,
                            token = args.token
                        )
                        navController.popBackStack()
                        tokenFormViewModel.showDuplicateTokenDialog.value =
                            TokenFormViewModel.DuplicateTokenDialogArgs(false)
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
                    colorValue = state.thumbnailColor,
                    onColorChanged = { color ->
                        keyboardController?.hide()
                        tokenFormViewModel.onEvent(TokenFormEvent.ThumbnailColorChanged(color))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OtpTypeDropdown(
                    value = state.type.name,
                    onItemSelected = {
                        tokenFormViewModel.onEvent(TokenFormEvent.TypeChanged(it))
                    }
                )

                // Issuer Field
                StyledTextField(
                    value = state.issuer,
                    onValueChange = { tokenFormViewModel.onEvent(TokenFormEvent.IssuerChanged(it)) },
                    label = stringResource(R.string.label_issuer),
                    placeholder = stringResource(R.string.hint_issuer),
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
                    onValueChange = { tokenFormViewModel.onEvent(TokenFormEvent.LabelChanged(it)) },
                    label = stringResource(R.string.label_label),
                    placeholder = stringResource(R.string.hint_label),
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
                        onValueChange = { tokenFormViewModel.onEvent(TokenFormEvent.SecretKeyChanged(it)) },
                        label = stringResource(R.string.label_secret_key),
                        placeholder = stringResource(R.string.hint_secret_key),
                        errorMessage = state.validationErrors["secretKey"],
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FormAdvancedOptions(
                        showAdvancedOptions = state.enableAdvancedOptions,
                        onShowAdvancedOptions = {
                            keyboardController?.hide()
                            tokenFormViewModel.onEvent(TokenFormEvent.EnableAdvancedOptionsChanged(it))
                        },
                        type = state.type,
                        algorithm = state.algorithm,
                        onAlgorithmChange = {
                            tokenFormViewModel.onEvent(TokenFormEvent.AlgorithmChanged(it))
                        },
                        period = state.period,
                        periodError = state.validationErrors["period"],
                        onPeriodChange = {
                            tokenFormViewModel.onEvent(TokenFormEvent.PeriodChanged(it))
                        },
                        digits = state.digits,
                        digitsError = state.validationErrors["digits"],
                        onDigitsChange = {
                            tokenFormViewModel.onEvent(TokenFormEvent.DigitsChanged(it))
                        },
                        counter = state.counter,
                        counterError = state.validationErrors["counter"],
                        onCounterChange = {
                            tokenFormViewModel.onEvent(TokenFormEvent.CounterChanged(it))
                        }
                    )
                }
            }

            val buttonText = if (tokenSetupMode == TokenSetupMode.UPDATE) stringResource(R.string.label_update_account)
            else stringResource(R.string.label_add_account)

            Button(
                onClick = {
                    keyboardController?.hide()
                    tokenFormViewModel.onEvent(TokenFormEvent.Submit)
                },
                shape = RoundedCornerShape(radiusTiny),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Composable
private fun OtpTypeDropdown(
    value: String,
    onItemSelected: (OTPType) -> Unit,
) {
    Box {
        var showTypeDropdown by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable { showTypeDropdown = true }
                .padding(10.dp)
        ) {
            Text(text = value, modifier = Modifier.widthIn(min = 75.dp))
            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(
            expanded = showTypeDropdown,
            onDismissRequest = {
                showTypeDropdown = false
            }
        ) {
            OTPType.entries.forEach {
                DropdownMenuItem(
                    text = { Text(it.name) },
                    onClick = {
                        onItemSelected(it)
                        showTypeDropdown = false
                    }
                )
            }
        }
    }
}

fun handleFormSuccess(
    token: TokenEntry,
    tokensViewModel: TokensViewModel,
    tokenFormViewModel: TokenFormViewModel,
    navController: NavController,
) {
    val requestCode = UUID.randomUUID().toString()
    tokensViewModel.upsertToken(
        token = token,
        requestCode = requestCode,
        onComplete = { responseCode ->
            if (requestCode == responseCode) {
                navController.popBackStack()
            }
        },
        onDuplicate = { responseCode, existingToken ->
            if (requestCode == responseCode) {
                tokenFormViewModel.showDuplicateTokenDialog.value =
                    TokenFormViewModel.DuplicateTokenDialogArgs(
                        true, token, existingToken
                    )
            }
        })
}

@Composable
fun TokenDeleteDialog(
    tokenId: String,
    state: TokenFormState,
    tokensViewModel: TokensViewModel,
    tokenFormViewModel: TokenFormViewModel,
    navController: NavController,
) {
    TokkyDialog(
        dialogTitle = stringResource(R.string.remove_account),
        dialogBody = stringResource(
            R.string.dialog_message_delete_token,
            "${state.issuer}${if (state.label.isNotBlank()) " (${state.label})" else ""}"
        ),
        confirmText = stringResource(R.string.remove),
        icon = {
            Icon(
                Icons.Outlined.WarningAmber,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        onDismissRequest = {
            tokenFormViewModel.showDeleteTokenDialog.value = false
        },
        onConfirmation = {
            val requestCode = UUID.randomUUID().toString()
            tokensViewModel.deleteToken(
                tokenId = tokenId,
                requestCode = requestCode,
                onComplete = { responseCode ->
                    if (requestCode == responseCode) {
                        navController.popBackStack()
                        tokenFormViewModel.showDeleteTokenDialog.value = false
                    }
                }
            )
        }
    )
}

@Composable
fun FormAdvancedOptions(
    showAdvancedOptions: Boolean,
    onShowAdvancedOptions: (Boolean) -> Unit,
    type: OTPType,
    algorithm: String,
    onAlgorithmChange: (String) -> Unit,
    period: String,
    periodError: String?,
    onPeriodChange: (String) -> Unit,
    digits: String,
    digitsError: String?,
    onDigitsChange: (String) -> Unit,
    counter: String,
    counterError: String?,
    onCounterChange: (String) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Toggle(
            state = showAdvancedOptions,
            onToggle = onShowAdvancedOptions,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        AnimatedVisibility(
            visible = showAdvancedOptions,
            enter = expandVertically(animationSpec = tween(200)),
            exit = shrinkVertically(animationSpec = tween(200))
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.label_algorithm),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(vertical = 5.dp, horizontal = 15.dp)
                )
                MultiToggleButton(
                    toggleStates = listOf("SHA1", "SHA256", "SHA512"),
                    currentSelection = algorithm,
                    onToggleChange = onAlgorithmChange
                )

                StyledTextField(
                    value = digits,
                    onValueChange = onDigitsChange,
                    label = stringResource(R.string.label_digits),
                    placeholder = stringResource(R.string.hint_digits),
                    errorMessage = digitsError,
                )

                if (type == OTPType.TOTP) {
                    StyledTextField(
                        value = period,
                        onValueChange = onPeriodChange,
                        label = stringResource(R.string.label_period),
                        placeholder = stringResource(R.string.hint_period),
                        errorMessage = periodError,
                    )
                }

                if (type == OTPType.HOTP) {
                    StyledTextField(
                        value = counter,
                        onValueChange = onCounterChange,
                        label = stringResource(R.string.label_counter),
                        placeholder = stringResource(R.string.hint_counter),
                        errorMessage = counterError,
                    )
                }
            }
        }
    }
}

@Composable
private fun Toggle(
    state: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = modifier.fillMaxWidth()
    ) {
        val progress by animateFloatAsState(
            targetValue = if (state) 90f else -90f,
            animationSpec = tween(durationMillis = 200),
            label = "AdvancedOptionsTransition"
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable { onToggle(!state) }
                .padding(10.dp)
        ) {
            Text(
                text = stringResource(R.string.label_advanced_options),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Rounded.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .rotate(progress),
            )
        }
    }
}