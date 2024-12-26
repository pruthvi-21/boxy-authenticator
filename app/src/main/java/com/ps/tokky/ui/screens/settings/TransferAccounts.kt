package com.ps.tokky.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.jw.preferences.Preference
import com.jw.preferences.PreferenceCategory
import com.ps.tokky.R
import com.ps.tokky.navigation.RouteBuilder
import com.ps.tokky.utils.Constants

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
