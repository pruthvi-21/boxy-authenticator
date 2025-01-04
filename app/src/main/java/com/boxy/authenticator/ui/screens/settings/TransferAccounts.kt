package com.boxy.authenticator.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.boxy.authenticator.R
import com.boxy.authenticator.navigation.RouteBuilder
import com.boxy.authenticator.utils.Constants
import com.boxy.preferences.Preference
import com.boxy.preferences.PreferenceCategory

@Composable
fun TransferAccounts(
    navController: NavController,
) {
    val readFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { result ->
        if (result != null) {
            navController.navigate(RouteBuilder.import(result))
        }
    }

    PreferenceCategory(
        title = { Text(stringResource(R.string.preference_category_transfer_accounts)) },
    ) {
        Preference(
            title = { Text(text = stringResource(R.string.export_accounts)) },
            summary = { Text(text = stringResource(R.string.export_accounts_summary)) },
            onClick = {
                navController.navigate(RouteBuilder.export())
            },
        )
        Preference(
            title = { Text(text = stringResource(R.string.import_accounts)) },
            summary = { Text(text = stringResource(R.string.import_accounts_summary)) },
            onClick = {
                readFileLauncher.launch(arrayOf(Constants.BACKUP_FILE_MIME_TYPE))
            },
            showDivider = false,
        )
    }
}
