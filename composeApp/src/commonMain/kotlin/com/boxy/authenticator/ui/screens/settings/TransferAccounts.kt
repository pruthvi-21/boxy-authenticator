package com.boxy.authenticator.ui.screens.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.export_accounts
import boxy_authenticator.composeapp.generated.resources.export_accounts_summary
import boxy_authenticator.composeapp.generated.resources.import_accounts
import boxy_authenticator.composeapp.generated.resources.import_accounts_summary
import boxy_authenticator.composeapp.generated.resources.last_backup_on
import boxy_authenticator.composeapp.generated.resources.preference_category_transfer_accounts
import com.boxy.authenticator.ui.state.SettingsUiState
import com.boxy.authenticator.utils.formatMillis
import com.jw.preferences.Preference
import com.jw.preferences.PreferenceCategory
import org.jetbrains.compose.resources.stringResource

@Composable
fun TransferAccounts(
    uiState: SettingsUiState,
    navigateToExport: () -> Unit,
    navigateToImport: () -> Unit,
) {

    PreferenceCategory(
        title = { Text(stringResource(Res.string.preference_category_transfer_accounts)) },
    ) {
        Preference(
            title = { Text(text = stringResource(Res.string.export_accounts)) },
            summary = {
                if (uiState.settings.lastBackupTimestamp <= 0L) {
                    Text(text = stringResource(Res.string.export_accounts_summary))
                } else {
                    val formattedDate = formatMillis(uiState.settings.lastBackupTimestamp)
                    Text(stringResource(Res.string.last_backup_on, formattedDate))
                }
            },
            onClick = { navigateToExport() },
        )
        Preference(
            title = { Text(text = stringResource(Res.string.import_accounts)) },
            summary = { Text(text = stringResource(Res.string.import_accounts_summary)) },
            onClick = { navigateToImport() },
            showDivider = false,
        )
    }
}
