package com.boxy.authenticator.ui.screens

import android.net.Uri
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.boxy.authenticator.R
import com.boxy.authenticator.helpers.TokenFormValidator
import com.boxy.authenticator.navigation.Routes
import com.boxy.authenticator.ui.components.TokkyButton
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.components.dialogs.TokkyDialog
import com.boxy.authenticator.ui.viewmodels.ImportTokensViewModel
import com.boxy.authenticator.utils.popBackStackIfInRoute
import com.boxy.authenticator.utils.queryName
import com.boxy.authenticator.utils.toast
import com.boxy.design.components.StyledTextField
import org.koin.androidx.compose.koinViewModel


private const val TAG = "ImportTokensScreen"

@Composable
fun ImportTokensScreen(
    fileUri: Uri,
    navController: NavController,
) {
    val context = LocalContext.current
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val importTokensViewModel: ImportTokensViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        if (!importTokensViewModel.isFileUnlocked.value) {
            importTokensViewModel.showPasswordDialog.value = true
        }
    }

    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(R.string.import_accounts),
                showDefaultNavigationIcon = true,
                onNavigationIconClick = { onBackPressedDispatcher?.onBackPressed() }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
        ) {
            ShowPasswordDialog(
                fileUri = fileUri,
                show = importTokensViewModel.showPasswordDialog.value,
                onDismiss = {
                    importTokensViewModel.showPasswordDialog.value = false
                    navController.popBackStackIfInRoute(Routes.ImportTokens)
                },
                onConfirm = { password ->
                    importTokensViewModel.importAccountsFromFile(context, fileUri, password)

                    importTokensViewModel.showPasswordDialog.value = false
                    if (importTokensViewModel.importError.value != null) {
                        importTokensViewModel.importError.value!!.toast(context)
                        navController.popBackStackIfInRoute(Routes.ImportTokens)
                        return@ShowPasswordDialog
                    }

                }
            )

            ShowDuplicatesWarningDialog(importTokensViewModel) {
                importTokensViewModel.showDuplicateWarningDialog.value = false
                navController.popBackStackIfInRoute(Routes.ImportTokens)
            }

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(importTokensViewModel.tokensToImport.value, key = { it.token.id }) {
                    ImportListItem(
                        item = it,
                        importTokensViewModel = importTokensViewModel
                    )
                }
            }
            TokkyButton(
                onClick = {
                    if (importTokensViewModel.tokensToImport.value.any { it.isDuplicate }) {
                        importTokensViewModel.showDuplicateWarningDialog.value = true
                    } else {
                        importTokensViewModel.importAccounts {
                            importTokensViewModel.showDuplicateWarningDialog.value = false
                            navController.popBackStackIfInRoute(Routes.ImportTokens)
                        }
                    }
                },
                enabled = importTokensViewModel.tokensToImport.value.any { it.checked },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .heightIn(min = 46.dp)
            ) {
                Text(text = stringResource(R.string.import_label))
            }
        }
    }
}

@Composable
fun ShowDuplicatesWarningDialog(
    importTokensViewModel: ImportTokensViewModel,
    onSuccess: () -> Unit,
) {
    if (importTokensViewModel.showDuplicateWarningDialog.value) {
        val tokens = importTokensViewModel.tokensToImport.value
        val duplicateCount = tokens.count { it.isDuplicate }
        val nonDuplicateCount = tokens.size - duplicateCount

        TokkyDialog(
            dialogTitle = stringResource(R.string.warning),
            confirmText = stringResource(R.string.proceed),
            dismissText = stringResource(R.string.cancel),
            dialogBody = stringResource(
                R.string.duplicate_warning_message,
                nonDuplicateCount,
                duplicateCount
            ),
            onDismissRequest = {
                importTokensViewModel.showDuplicateWarningDialog.value = false
            },
            onConfirmation = {
                importTokensViewModel.importAccounts {
                    onSuccess()
                }
            }
        )
    }
}

@Composable
private fun ImportListItem(
    item: ImportTokensViewModel.ImportItem,
    importTokensViewModel: ImportTokensViewModel,
) {
    val context = LocalContext.current
    var showRenameDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(
                if (!item.isDuplicate) MaterialTheme.colorScheme.surfaceVariant
                else MaterialTheme.colorScheme.errorContainer
            )
            .clickable {
                importTokensViewModel.checkToken(item.token.id, !item.checked)
            }
            .padding(horizontal = 16.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Checkbox(
            checked = if (item.isDuplicate) false else item.checked,
            onCheckedChange = { importTokensViewModel.checkToken(item.token.id, it) }
        )
        Text(
            item.token.name,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
        )
        IconButton(onClick = {
            showRenameDialog = true
        }) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null
            )
        }
    }

    if (showRenameDialog) {
        val issuer = remember { mutableStateOf(item.token.issuer) }
        val label = remember { mutableStateOf(item.token.label) }

        val issuerError = remember { mutableStateOf<String?>(null) }

        TokkyDialog(
            dialogTitle = "Rename",
            onDismissRequest = { showRenameDialog = false },
            onConfirmation = {
                val validator = TokenFormValidator(context)
                val issuerResult = validator.validateIssuer(issuer.value)

                if (issuerResult.isValid) {
                    importTokensViewModel.updateToken(
                        tokenId = item.token.id,
                        issuer = issuer.value,
                        label = label.value,
                        onComplete = {
                            showRenameDialog = false
                        })
                } else {
                    issuerError.value = issuerResult.errorMessage
                }
            }
        ) {
            Column {
                StyledTextField(
                    value = issuer.value,
                    onValueChange = {
                        issuer.value = it
                        issuerError.value = null
                    },
                    placeholder = stringResource(R.string.hint_issuer),
                    errorMessage = issuerError.value
                )

                StyledTextField(
                    value = label.value,
                    onValueChange = { label.value = it },
                    placeholder = stringResource(R.string.hint_label)
                )
            }
        }
    }
}

@Composable
private fun ShowPasswordDialog(
    fileUri: Uri?,
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val context = LocalContext.current

    var password by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    if (show && fileUri != null) {
        val fileName = fileUri.queryName(context.contentResolver)
        TokkyDialog(
            dialogTitle = stringResource(R.string.decrypt_your_file),
            confirmText = stringResource(R.string.decrypt),
            onDismissRequest = {
                onDismiss()
                password = ""
            },
            onConfirmation = {
                onConfirm(password)
                password = ""
            },
            dialogBody = fileName
        ) {
            Column {
                StyledTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    placeholder = stringResource(R.string.enter_password_to_restore),
                    isPasswordField = true,
                    containerModifier = Modifier
                        .focusRequester(focusRequester)
                        .onGloballyPositioned {
                            focusRequester.requestFocus()
                        }
                )
            }
        }
    }
}