package com.ps.tokky.ui.screens

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ps.tokky.R
import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.databinding.DialogTitleDeleteWarningBinding
import com.ps.tokky.ui.components.DefaultAppBarNavigationIcon
import com.ps.tokky.ui.components.MultiToggleButton
import com.ps.tokky.ui.components.ThumbnailController
import com.ps.tokky.ui.components.TokkyScaffold
import com.ps.tokky.ui.viewmodels.TokenFormEvent
import com.ps.tokky.ui.viewmodels.TokenFormState
import com.ps.tokky.ui.viewmodels.TokenFormValidationEvent
import com.ps.tokky.ui.viewmodels.TokenFormViewModel
import com.ps.tokky.ui.viewmodels.TokensViewModel
import com.ps.tokky.utils.HashAlgorithm
import com.ps.tokky.utils.copy
import com.ps.tokky.utils.getInitials
import com.ps.tokky.utils.top
import java.util.UUID

private val radiusTiny = 4.dp

@Composable
fun TokenSetupScreen(
    tokenId: String? = null,
    tokensViewModel: TokensViewModel,
    tokenFormViewModel: TokenFormViewModel = hiltViewModel(),
    navController: NavController,
) {
    val context = LocalContext.current
    val localFocus = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var state = tokenFormViewModel.uiState.value
    var isInEditMode = tokenFormViewModel.isInEditMode

    LaunchedEffect(tokenId, tokensViewModel.tokenToEdit) {
        tokenId?.let {
            tokensViewModel.tokenToEdit?.takeIf { it.id == tokenId }?.let { token ->
                tokenFormViewModel.setInitialStateFromToken(token)
                tokenFormViewModel.isInEditMode = true
                tokenFormViewModel.tokenToEdit = tokensViewModel.tokenToEdit

                state = tokenFormViewModel.uiState.value
                isInEditMode = tokenFormViewModel.isInEditMode
            }
        }
    }

    LaunchedEffect(context, isInEditMode) {
        tokenFormViewModel.validationEvent.collect { event ->
            if (event is TokenFormValidationEvent.Success) {
                handleFormSuccess(event.token, tokensViewModel, navController, context)
            }
        }
    }

    BackHandler(enabled = tokenFormViewModel.isFormUpdated()) {
        showBackPressDialog(context, navController)
    }

    TokkyScaffold(
        topBar = {
            Toolbar(
                isInEditMode,
                onDelete = {
                    if (isInEditMode) deleteToken(
                        tokenFormViewModel.tokenToEdit!!,
                        tokensViewModel,
                        navController,
                        context
                    )
                }
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

                if (!isInEditMode) {
                    // Secret Key Field
                    StyledTextField(
                        value = state.secretKey,
                        onValueChange = { tokenFormViewModel.onEvent(TokenFormEvent.SecretKeyChanged(it)) },
                        label = stringResource(R.string.label_secret_key),
                        placeholder = stringResource(R.string.hint_secret_key),
                        errorMessage = state.validationErrors["secretKey"],
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AdvancedOptionsToggle(state, tokenFormViewModel, keyboardController)

                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedVisibility(
                        visible = state.enableAdvancedOptions,
                        enter = expandVertically(animationSpec = tween(200)),
                        exit = shrinkVertically(animationSpec = tween(200))
                    ) {
                        AdvancedOptions(tokenFormViewModel)
                    }
                }
            }

            SubmitButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                onClick = {
                    keyboardController?.hide()
                    tokenFormViewModel.onEvent(TokenFormEvent.Submit)
                },
                isInEditMode = isInEditMode
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    isInEditMode: Boolean,
    onDelete: () -> Unit
) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val title = if (isInEditMode) stringResource(R.string.title_update_account_details)
    else stringResource(R.string.title_enter_account_details)

    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            DefaultAppBarNavigationIcon {
                backPressedDispatcher?.onBackPressed()
            }
        },
        actions = {
            if (isInEditMode) {
                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        windowInsets = WindowInsets.safeDrawing.copy(
            bottom = 0.dp,
            top = WindowInsets.safeDrawing.asPaddingValues()
                .top() + dimensionResource(R.dimen.toolbar_margin_top)
        ),
    )
}

@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    modifier: Modifier = Modifier,
) {
    val hasError = !errorMessage.isNullOrEmpty()

    Column(modifier = modifier) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 15.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            placeholder = { Text(placeholder) },
            isError = hasError,
            shape = RoundedCornerShape(radiusTiny),
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
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (hasError) {
            Text(
                text = errorMessage!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun SubmitButton(
    modifier: Modifier,
    onClick: () -> Unit,
    isInEditMode: Boolean
) {
    val buttonText = if (isInEditMode) stringResource(R.string.label_update_account)
    else stringResource(R.string.label_add_account)

    Button(
        modifier = modifier,
        shape = RoundedCornerShape(radiusTiny),
        onClick = onClick,
    ) {
        Text(text = buttonText)
    }
}

fun handleFormSuccess(
    token: TokenEntry,
    tokensViewModel: TokensViewModel,
    navController: NavController,
    context: Context
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
                MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.account_exists_dialog_title)
                    .setMessage(
                        context.getString(
                            R.string.account_exists_dialog_message,
                            token.name
                        )
                    )
                    .setPositiveButton(R.string.replace) { _, _ ->
                        tokensViewModel.replaceExistingToken(
                            existingToken = existingToken,
                            token = token
                        )
                        navController.popBackStack()
                    }
                    .setNegativeButton(R.string.rename) { _, _ ->

                    }
                    .create()
                    .show()
            }
        })
}

private fun deleteToken(
    token: TokenEntry,
    tokensViewModel: TokensViewModel,
    navController: NavController,
    context: Context
) {
    val titleViewBinding = DialogTitleDeleteWarningBinding.inflate(LayoutInflater.from(context))

    val ssb = SpannableStringBuilder(token.issuer)
    ssb.setSpan(
        StyleSpan(Typeface.BOLD),
        0,
        token.issuer.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    titleViewBinding.title.text =
        SpannableStringBuilder(context.getString(R.string.remove_account))
            .append(" ")
            .append(ssb)
            .append("?")

    MaterialAlertDialogBuilder(context)
        .setCustomTitle(titleViewBinding.root)
        .setMessage(R.string.dialog_message_delete_token)
        .setPositiveButton(R.string.remove) { _, _ ->
            val requestCode = UUID.randomUUID().toString()
            tokensViewModel.deleteToken(
                tokenId = token.id,
                requestCode = requestCode,
                onComplete = { responseCode ->
                    if (requestCode == responseCode) {
                        navController.popBackStack()
                    }
                })
        }
        .setNegativeButton(R.string.cancel, null)
        .create()
        .show()


}

fun showBackPressDialog(
    context: Context,
    navController: NavController
) {
    MaterialAlertDialogBuilder(context)
        .setMessage(R.string.message_unsaved_changes)
        .setPositiveButton(R.string.go_back) { _, _ ->
            navController.navigateUp()
        }
        .setNegativeButton(R.string.cancel, null)
        .show()
}

@Composable
fun AdvancedOptionsToggle(
    state: TokenFormState,
    tokenFormViewModel: TokenFormViewModel,
    keyboardController: SoftwareKeyboardController?
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val progress by animateFloatAsState(
            targetValue = if (state.enableAdvancedOptions) 90f else -90f,
            animationSpec = tween(durationMillis = 200),
            label = "AdvancedOptionsTransition"
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(radiusTiny))
                .clickable {
                    keyboardController?.hide()
                    tokenFormViewModel.onEvent(TokenFormEvent.EnableAdvancedOptionsChanged(!state.enableAdvancedOptions))
                }
                .padding(10.dp)
        ) {
            Text(
                text = stringResource(R.string.label_advanced_options),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(Modifier.width(10.dp))
            Icon(
                painter = painterResource(R.drawable.ic_samsung_arrow_left),
                contentDescription = null,
                modifier = Modifier.rotate(progress)
            )
        }
    }
}

@Composable
fun AdvancedOptions(formViewModel: TokenFormViewModel) {
    val state = formViewModel.uiState.value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.label_algorithm),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 15.dp)
        )
        MultiToggleButton(
            toggleStates = HashAlgorithm.entries.map { it.name },
            currentSelection = state.algorithm.name,
            onToggleChange = {
                formViewModel.onEvent(TokenFormEvent.AlgorithmChanged(HashAlgorithm.valueOf(it)))
            }
        )
        Spacer(Modifier.height(10.dp))

        StyledTextField(
            value = formViewModel.uiState.value.period,
            onValueChange = {
                formViewModel.onEvent(TokenFormEvent.PeriodChanged(it))
            },
            label = stringResource(R.string.label_period),
            placeholder = stringResource(R.string.hint_period),
        )

        Spacer(Modifier.height(10.dp))

        StyledTextField(
            value = formViewModel.uiState.value.digits,
            onValueChange = {
                formViewModel.onEvent(TokenFormEvent.DigitsChanged(it))
            },
            label = stringResource(R.string.label_digits),
            placeholder = stringResource(R.string.hint_digits),
        )
    }
}
