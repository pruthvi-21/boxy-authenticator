package com.boxy.authenticator.ui.screens.settings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.enter_password_to_decrypt
import boxy_authenticator.composeapp.generated.resources.enter_your_password
import boxy_authenticator.composeapp.generated.resources.export_accounts
import boxy_authenticator.composeapp.generated.resources.export_accounts_summary
import boxy_authenticator.composeapp.generated.resources.import_accounts
import boxy_authenticator.composeapp.generated.resources.import_accounts_summary
import boxy_authenticator.composeapp.generated.resources.incorrect_password
import boxy_authenticator.composeapp.generated.resources.no_tokens_to_import
import boxy_authenticator.composeapp.generated.resources.preference_category_transfer_accounts
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.ui.components.dialogs.RequestPasswordDialog
import com.boxy.authenticator.ui.viewmodels.TransferAccountsViewModel
import com.jw.preferences.Preference
import com.jw.preferences.PreferenceCategory
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun TransferAccounts(
    snackbarHostState: SnackbarHostState,
    onNavigateToExport: () -> Unit,
    onNavigateToImport: (List<TokenEntry>) -> Unit,
) {
    val transferAccountsViewModel: TransferAccountsViewModel = koinInject()

    PreferenceCategory(
        title = { Text(stringResource(Res.string.preference_category_transfer_accounts)) },
    ) {
        Preference(
            title = { Text(text = stringResource(Res.string.export_accounts)) },
            summary = { Text(text = stringResource(Res.string.export_accounts_summary)) },
            enabled = transferAccountsViewModel.areTokensAvailable() != 0L,
            onClick = { onNavigateToExport() },
        )
        Preference(
            title = { Text(text = stringResource(Res.string.import_accounts)) },
            summary = { Text(text = stringResource(Res.string.import_accounts_summary)) },
            onClick = {
                transferAccountsViewModel.importTokensFromFile(
                    onSuccess = {
                        if (it.isNotEmpty()) {
                            onNavigateToImport(it)
                        } else {
                            snackbarHostState.showSnackbar(getString(Res.string.no_tokens_to_import))
                        }
                    },
                    onFailure = {
                        snackbarHostState.showSnackbar(getString(Res.string.incorrect_password))
                    }
                )
            },
            showDivider = false,
        )
    }

    if (transferAccountsViewModel.requestForPassword) {
        RequestPasswordDialog(
            title = stringResource(Res.string.enter_your_password),
            placeholder = stringResource(Res.string.enter_password_to_decrypt),
            onDismissRequest = {
                transferAccountsViewModel.setRequestForPassword(false)
            },
            onConfirmation = {
                transferAccountsViewModel.tryDecrypt(it,
                    onSuccess = {
                        transferAccountsViewModel.setRequestForPassword(false)
                        onNavigateToImport(it)
                    }, onFailed = {
                        snackbarHostState.showSnackbar(getString(Res.string.incorrect_password))
                    }
                )
            }
        )
    }
}
