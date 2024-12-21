package com.ps.tokky.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ps.camerax.BarcodeScanningActivity
import com.ps.camerax.BarcodeScanningActivity.Companion.SCAN_RESULT
import com.ps.tokky.R
import com.ps.tokky.navigation.RouteBuilder
import com.ps.tokky.ui.components.ExpandableFab
import com.ps.tokky.ui.components.TokensList
import com.ps.tokky.ui.components.TokkyScaffold
import com.ps.tokky.ui.viewmodels.TokensViewModel
import com.ps.tokky.utils.Utils
import com.ps.tokky.utils.copy
import com.ps.tokky.utils.toast

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    tokensViewModel: TokensViewModel,
    navController: NavController,
) {
    val context = LocalContext.current

    tokensViewModel.fetchTokens()
    val tokensState by tokensViewModel.tokensState.collectAsStateWithLifecycle()

    TokkyScaffold { safePadding ->
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(safePadding.copy(top = 0.dp))
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    colors = TopAppBarDefaults.topAppBarColors().copy(
                        navigationIconContentColor = primaryColor,
                        titleContentColor = primaryColor,
                    ),
                    windowInsets = WindowInsets(
                        top = safePadding.calculateTopPadding() + dimensionResource(R.dimen.toolbar_margin_top)
                    ),
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(RouteBuilder.settings())
                        }) {
                            Image(
                                painterResource(R.drawable.ic_settings),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                            )
                        }
                    }
                )

                when (val uiState = tokensState) {
                    is TokensViewModel.UIState.Loading -> {}

                    is TokensViewModel.UIState.Success -> {
                        val tokens = uiState.data

                        if (tokens.isNotEmpty()) {
                            TokensList(
                                tokens,
                                onEdit = {
                                    tokensViewModel.tokenToEdit = it
                                    navController.navigate(RouteBuilder.tokenSetup(it.id))
                                })
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.empty_layout_text),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .padding(horizontal = 36.dp),
                                    textAlign = TextAlign.Center,
                                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5f
                                )
                            }
                        }
                    }
                }
            }
            FAB(safePadding, navController)
        }
    }
}

@Composable
private fun FAB(
    windowPadding: PaddingValues,
    navController: NavController
) {
    var isFabExpanded by remember { mutableStateOf(false) }

    BackHandler(enabled = isFabExpanded) {
        isFabExpanded = false
    }

    val context = LocalContext.current

    val addNewQRActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val extras = result.data?.extras
        if (result.resultCode == Activity.RESULT_OK && extras != null) {
            val auth = extras.getString(SCAN_RESULT)
            if (Utils.isValidTOTPAuthURL(auth)) {
//                navController.navigate(R.id.action_home_fragment_to_token_details_fragment)
            } else {
                context.getString(R.string.error_bad_formed_otp_url).toast(context)
            }
        }
    }

    ExpandableFab(
        isFabExpanded = isFabExpanded,
        windowPadding = windowPadding,
        onFabClick = {
            isFabExpanded = !isFabExpanded
        },
        onScrimClick = {
            isFabExpanded = false
        },
        onQrClick = {
            val intent = Intent(context, BarcodeScanningActivity::class.java)
            addNewQRActivityLauncher.launch(intent)
            isFabExpanded = false
        },
        onManualClick = {
            navController.navigate(RouteBuilder.tokenSetup())
            isFabExpanded = false
        }
    )


}