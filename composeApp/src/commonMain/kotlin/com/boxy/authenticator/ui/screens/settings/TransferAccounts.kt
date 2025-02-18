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
import com.boxy.authenticator.domain.usecases.FetchTokenCountUseCase
import com.boxy.authenticator.ui.components.dialogs.RequestPasswordDialog
import com.jw.preferences.Preference
import com.jw.preferences.PreferenceCategory
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun TransferAccounts(
    snackbarHostState: SnackbarHostState,
    onNavigateToExport: () -> Unit,
    onNavigateToImport: () -> Unit,
) {
    PreferenceCategory(
        title = { Text(stringResource(Res.string.preference_category_transfer_accounts)) },
    ) {
        Preference(
            title = { Text(text = stringResource(Res.string.export_accounts)) },
            summary = { Text(text = stringResource(Res.string.export_accounts_summary)) },
            enabled = areTokensAvailable() != 0L,
            onClick = { onNavigateToExport() },
        )
        Preference(
            title = { Text(text = stringResource(Res.string.import_accounts)) },
            summary = { Text(text = stringResource(Res.string.import_accounts_summary)) },
            onClick = { onNavigateToImport() },
            showDivider = false,
        )
    }
}

@Composable
fun areTokensAvailable(fetchTokenCountUseCase: FetchTokenCountUseCase = koinInject()): Long {
    return fetchTokenCountUseCase.invoke().fold(onSuccess = { it }, onFailure = { 0 })
}
