package com.boxy.authenticator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.cancel
import boxy_authenticator.composeapp.generated.resources.duplicate_warning_message
import boxy_authenticator.composeapp.generated.resources.hint_issuer
import boxy_authenticator.composeapp.generated.resources.hint_label
import boxy_authenticator.composeapp.generated.resources.import_accounts
import boxy_authenticator.composeapp.generated.resources.import_label
import boxy_authenticator.composeapp.generated.resources.proceed
import boxy_authenticator.composeapp.generated.resources.rename
import boxy_authenticator.composeapp.generated.resources.warning
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.core.TokenFormValidator
import com.boxy.authenticator.navigation.components.ImportTokensScreenComponent
import com.boxy.authenticator.ui.components.StyledTextField
import com.boxy.authenticator.ui.components.TokkyButton
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.components.dialogs.PlatformAlertDialog
import com.boxy.authenticator.ui.components.dialogs.TokkyDialog
import com.boxy.authenticator.ui.viewmodels.ImportItem
import com.boxy.authenticator.ui.viewmodels.ImportTokensViewModel
import com.boxy.authenticator.utils.name
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

private const val TAG = "ImportTokensScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportTokensScreen(
    component: ImportTokensScreenComponent,
) {
    val importTokensViewModel = component.importTokensViewModel
    val tokensToImport = importTokensViewModel.tokensToImport

    LaunchedEffect(Unit) {
        importTokensViewModel.setTokens(component.tokens)
    }

    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(Res.string.import_accounts),
                showDefaultNavigationIcon = true,
                onNavigationIconClick = { component.navigateUp() }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
        ) {
            DuplicateTokensWarningDialog(
                showDialog = importTokensViewModel.showDuplicateWarningDialog.value,
                tokensToImport = tokensToImport,
                onDismissRequest = {
                    importTokensViewModel.showDuplicateWarningDialog.value = false
                },
                onConfirmRequest = {
                    importTokensViewModel.importAccounts(tokensToImport) {
                        importTokensViewModel.showDuplicateWarningDialog.value = false
                        component.navigateUp()
                    }
                }
            )

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(tokensToImport, key = { it.token.id }) {
                    ImportListItem(
                        item = it,
                        importTokensViewModel = importTokensViewModel
                    )
                }
            }
            TokkyButton(
                onClick = {
                    if (tokensToImport.any { it.isDuplicate }) {
                        importTokensViewModel.showDuplicateWarningDialog.value = true
                    } else {
                        importTokensViewModel.importAccounts(tokensToImport) {
                            importTokensViewModel.showDuplicateWarningDialog.value = false
                            component.navigateUp()
                        }
                    }
                },
                enabled = tokensToImport.any { it.isChecked },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .heightIn(min = 46.dp)
            ) {
                Text(text = stringResource(Res.string.import_label))
            }
        }
    }
}

@Composable
private fun DuplicateTokensWarningDialog(
    showDialog: Boolean,
    tokensToImport: List<ImportItem>,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
) {
    if (showDialog) {
        val duplicateCount = tokensToImport.count { it.isDuplicate }
        val nonDuplicateCount = tokensToImport.size - duplicateCount

        PlatformAlertDialog(
            title = stringResource(Res.string.warning),
            message = stringResource(
                Res.string.duplicate_warning_message,
                nonDuplicateCount,
                duplicateCount
            ),
            confirmText = stringResource(Res.string.proceed),
            dismissText = stringResource(Res.string.cancel),
            onDismissRequest = onDismissRequest,
            onConfirmation = onConfirmRequest,
        )
    }
}

@Composable
private fun RenameTokenDialog(
    showDialog: Boolean,
    token: TokenEntry,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (String, String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    if (showDialog) {
        val issuer = remember { mutableStateOf(token.issuer) }
        val label = remember { mutableStateOf(token.label) }

        val issuerError = remember { mutableStateOf<String?>(null) }

        TokkyDialog(
            dialogTitle = stringResource(Res.string.rename),
            onDismissRequest = onDismissRequest,
            onConfirmation = {
                val validator = TokenFormValidator()
                when (val issuerResult = validator.validateIssuer(issuer.value)) {
                    is TokenFormValidator.Result.Failure -> {
                        scope.launch {
                            issuerError.value = getString(issuerResult.errorMessage)
                        }
                    }

                    is TokenFormValidator.Result.Success -> {
                        onConfirmRequest(issuer.value, label.value)
                    }
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
                    placeholder = stringResource(Res.string.hint_issuer),
                    errorMessage = issuerError.value
                )
                Spacer(Modifier.height(10.dp))

                StyledTextField(
                    value = label.value,
                    onValueChange = { label.value = it },
                    placeholder = stringResource(Res.string.hint_label)
                )
            }
        }
    }
}

@Composable
private fun ImportListItem(
    item: ImportItem,
    importTokensViewModel: ImportTokensViewModel,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(
                    if (!item.isDuplicate) MaterialTheme.colorScheme.surface
                    else MaterialTheme.colorScheme.errorContainer
                )
                .clickable { importTokensViewModel.toggleToken(item) }
                .padding(horizontal = 16.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = { importTokensViewModel.toggleToken(item) }
            )
            Text(
                item.token.name,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
            )
            IconButton(onClick = {
                importTokensViewModel.showRenameTokenDialogWithId.value = item.token.id
            }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null
                )
            }
        }
        HorizontalDivider()
    }

    RenameTokenDialog(
        showDialog = item.token.id == importTokensViewModel.showRenameTokenDialogWithId.value,
        token = item.token,
        onDismissRequest = {
            importTokensViewModel.showRenameTokenDialogWithId.value = null
        },
        onConfirmRequest = { issuer, label ->
            importTokensViewModel.updateToken(
                token = item.token,
                issuer = issuer,
                label = label,
                onComplete = {
                    importTokensViewModel.showRenameTokenDialogWithId.value = null
                })
        }
    )
}
