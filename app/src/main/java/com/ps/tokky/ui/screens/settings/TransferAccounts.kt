package com.ps.tokky.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.ps.tokky.R
import com.ps.tokky.navigation.RouteBuilder
import com.ps.tokky.ui.components.preferences.PreferenceCategory
import com.ps.tokky.ui.components.preferences.preference
import com.ps.tokky.utils.Constants

@Composable
fun TransferAccounts(
    navController: NavController,
) {
    val context = LocalContext.current

    val readFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { result ->
        if (result != null) {
            navController.navigate(RouteBuilder.import(result))
        }
    }

    PreferenceCategory(
        title = context.getString(R.string.preference_category_transfer_accounts),
    ) {
        preference(
            title = context.getString(R.string.export_accounts),
            summary = context.getString(R.string.export_accounts_summary),
            onClick = {
                navController.navigate(RouteBuilder.export())
            }
        )
        preference(
            title = context.getString(R.string.import_accounts),
            summary = context.getString(R.string.import_accounts_summary),
            onClick = {
                readFileLauncher.launch(arrayOf(Constants.BACKUP_FILE_MIME_TYPE))
            }
        )
    }
}
