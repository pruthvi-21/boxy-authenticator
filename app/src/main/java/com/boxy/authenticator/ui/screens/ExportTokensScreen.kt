package com.boxy.authenticator.ui.screens

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.boxy.authenticator.R
import com.boxy.authenticator.ui.components.TokkyButton
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.viewmodels.ExportTokensViewModel
import com.boxy.authenticator.utils.Constants
import com.boxy.authenticator.utils.Constants.BACKUP_FILE_MIME_TYPE
import com.boxy.design.components.StyledTextField
import org.koin.androidx.compose.koinViewModel

private const val TAG = "ExportTokensScreen"

@Composable
fun ExportTokensScreen() {
    val context = LocalContext.current
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val exportViewModel: ExportTokensViewModel = koinViewModel()

    val exportFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val intent = result.data
        if (result.resultCode == Activity.RESULT_OK && intent != null) {
            if (intent.data == null) return@rememberLauncherForActivityResult

            exportViewModel.exportTokens(context, intent.data!!) { status ->
                if (status) {
                    Toast.makeText(context, R.string.export_success, Toast.LENGTH_SHORT)
                        .show()
                    Log.i(TAG, "Accounts exported")
                } else {
                    Toast.makeText(context, R.string.export_error, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Accounts exported failed")
                }
                onBackPressedDispatcher?.onBackPressed()
            }
        } else {
            Log.e("TAG", "Some Error Occurred : $result")
        }
    }

    var showPassword by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(R.string.export_accounts),
                showDefaultNavigationIcon = true,
                onNavigationIconClick = { onBackPressedDispatcher?.onBackPressed() }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            StyledTextField(
                value = exportViewModel.password.value,
                onValueChange = {
                    exportViewModel.updatePassword(it)
                },
                label = stringResource(R.string.password),
                placeholder = stringResource(R.string.enter_password_for_backup),
                isPasswordField = !showPassword,
                hidePasswordVisibilityEye = true,
                errorMessage = exportViewModel.passwordError.value,
            )
            Spacer(Modifier.height(10.dp))
            StyledTextField(
                value = exportViewModel.confirmPassword.value,
                onValueChange = {
                    exportViewModel.updateConfirmPassword(it)
                },
                label = stringResource(R.string.confirm_password),
                placeholder = stringResource(R.string.reenter_above_password),
                isPasswordField = !showPassword,
                hidePasswordVisibilityEye = true,
                errorMessage = exportViewModel.confirmPasswordError.value,
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .clickable {
                        showPassword = !showPassword
                    }
                    .padding(end = 15.dp)
                    .align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = showPassword,
                    onCheckedChange = { showPassword = !showPassword },
                )

                Text(stringResource(R.string.show_password))
            }

            TokkyButton(
                onClick = {
                    if (exportViewModel.verifyFields(context)) {
                        val createDocumentIntent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = BACKUP_FILE_MIME_TYPE
                            putExtra(Intent.EXTRA_TITLE, "${Constants.EXPORT_FILE_NAME}.txt")
                        }
                        exportFileLauncher.launch(createDocumentIntent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .heightIn(min = 46.dp)
            ) {
                Text(stringResource(R.string.export))
            }
        }
    }
}
