package com.ps.tokky.ui.screens

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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ps.tokky.R
import com.ps.tokky.navigation.Routes
import com.ps.tokky.ui.components.DefaultAppBarNavigationIcon
import com.ps.tokky.ui.components.StyledTextField
import com.ps.tokky.ui.components.TokkyScaffold
import com.ps.tokky.ui.components.dialogs.TokkyDialog
import com.ps.tokky.ui.viewmodels.ImportViewModel
import com.ps.tokky.ui.viewmodels.TokenFormValidator
import com.ps.tokky.utils.popBackStackIfInRoute
import com.ps.tokky.utils.toast

private const val TAG = "ImportTokensScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportTokensScreen(
    fileUri: Uri,
    navController: NavController,
) {
    val context = LocalContext.current
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val importViewModel: ImportViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        if (!importViewModel.isFileUnlocked.value) {
            importViewModel.showPasswordDialog.value = true
        }
    }

    TokkyScaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.import_accounts),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    DefaultAppBarNavigationIcon {
                        onBackPressedDispatcher?.onBackPressed()
                    }
                }
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
                show = importViewModel.showPasswordDialog.value,
                onDismiss = {
                    importViewModel.showPasswordDialog.value = false
                    navController.popBackStackIfInRoute(Routes.ImportTokens)
                },
                onConfirm = { password ->
                    importViewModel.importAccountsFromFile(context, fileUri, password)

                    importViewModel.showPasswordDialog.value = false
                    if (importViewModel.importError.value != null) {
                        importViewModel.importError.value!!.toast(context)
                        navController.popBackStackIfInRoute(Routes.ImportTokens)
                        return@ShowPasswordDialog
                    }

                }
            )

            ShowDuplicatesWarningDialog(importViewModel)

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(importViewModel.tokensToImport.value, key = { it.token.id }) {
                    ImportListItem(
                        item = it,
                        importViewModel = importViewModel
                    )
                }
            }
            Button(
                onClick = {
                    if (importViewModel.tokensToImport.value.any { it.isDuplicate }) {
                        importViewModel.showDuplicateWarningDialog.value = true
                    } else {
                        importViewModel.importAccounts()
                    }
                },
                shape = RoundedCornerShape(4.dp),
                enabled = importViewModel.tokensToImport.value.any { it.checked },
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
    importViewModel: ImportViewModel,
) {
    if (importViewModel.showDuplicateWarningDialog.value) {
        val tokens = importViewModel.tokensToImport.value
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
                importViewModel.showDuplicateWarningDialog.value = false
            },
            onConfirmation = {
                importViewModel.importAccounts()
            }
        )
    }
}

@Composable
private fun ImportListItem(
    item: ImportViewModel.ImportItem,
    importViewModel: ImportViewModel,
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
                importViewModel.checkToken(item.token.id, !item.checked)
            }
            .padding(horizontal = 16.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Checkbox(
            checked = if (item.isDuplicate) false else item.checked,
            onCheckedChange = { importViewModel.checkToken(item.token.id, it) }
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
                val validator = TokenFormValidator(context.resources)
                val issuerResult = validator.validateIssuer(issuer.value)

                if (issuerResult.isValid) {
                    importViewModel.updateToken(
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
    var password by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    if (show && fileUri != null) {
        val fileName = fileUri.path?.split(":")?.get(1)
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
                    modifier = Modifier
                        .padding(top = if (fileName != null) 20.dp else 0.dp)
                        .focusRequester(focusRequester)
                        .onGloballyPositioned {
                            focusRequester.requestFocus()
                        }
                )
            }
        }
    }
}