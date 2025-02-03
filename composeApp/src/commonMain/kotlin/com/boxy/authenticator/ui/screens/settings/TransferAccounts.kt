package com.boxy.authenticator.ui.screens.settings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.export_accounts
import boxy_authenticator.composeapp.generated.resources.export_accounts_summary
import boxy_authenticator.composeapp.generated.resources.import_accounts
import boxy_authenticator.composeapp.generated.resources.import_accounts_summary
import boxy_authenticator.composeapp.generated.resources.preference_category_transfer_accounts
import com.boxy.authenticator.data.models.TokenEntry
import com.boxy.authenticator.helpers.Logger
import com.boxy.authenticator.ui.viewmodels.TransferAccountsViewModel
import com.jw.preferences.Preference
import com.jw.preferences.PreferenceCategory
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

private const val TAG = "TransferAccounts"

@Composable
fun TransferAccounts(
    snackbarHostState: SnackbarHostState,
    onNavigateToImport: (List<TokenEntry>) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val transferAccountsViewModel: TransferAccountsViewModel = koinInject()

    PreferenceCategory(
        title = { Text(stringResource(Res.string.preference_category_transfer_accounts)) },
    ) {
        Preference(
            title = { Text(text = stringResource(Res.string.export_accounts)) },
            summary = { Text(text = stringResource(Res.string.export_accounts_summary)) },
            onClick = {
                transferAccountsViewModel.exportTokensToFile {
                    scope.launch {
                        if (it) {
                            snackbarHostState.showSnackbar("Accounts exported.")
                        } else {
                            snackbarHostState.showSnackbar("Failed to export accounts.")
                        }
                    }
                }
            },
        )
        Preference(
            title = { Text(text = stringResource(Res.string.import_accounts)) },
            summary = { Text(text = stringResource(Res.string.import_accounts_summary)) },
            onClick = {
                transferAccountsViewModel.importTokensFromFile(
                    onSuccess = {
                        onNavigateToImport(it)
                    },
                    onFailure = {
                        scope.launch {
                            Logger.e(TAG, it)
                            snackbarHostState.showSnackbar("Failed to import.")
                        }
                    }
                )
            },
            showDivider = false,
        )
    }
}
