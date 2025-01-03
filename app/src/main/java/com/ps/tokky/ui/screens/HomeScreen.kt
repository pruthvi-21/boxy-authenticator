package com.ps.tokky.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.ps.tokky.ui.components.Toolbar
import com.ps.tokky.ui.screens.home.TokensList
import com.ps.tokky.ui.viewmodels.HomeViewModel
import com.ps.tokky.utils.Utils
import com.ps.tokky.utils.copy
import com.ps.tokky.utils.toast
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
) {
    val homeViewModel: HomeViewModel = koinViewModel()

    val tokensState by homeViewModel.tokensState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        homeViewModel.loadTokens()
    }

    Scaffold { safePadding ->
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(safePadding.copy(top = 0.dp))
            ) {
                Toolbar(
                    title = stringResource(R.string.app_name),
                    showDefaultNavigationIcon = false,
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(RouteBuilder.settings())
                        }) {
                            Icon(
                                imageVector = Icons.TwoTone.Settings,
                                contentDescription = "",
                            )
                        }
                    },
                )
                when (val uiState = tokensState) {
                    is HomeViewModel.UIState.Loading -> {}

                    is HomeViewModel.UIState.Success -> {
                        val tokens = uiState.data

                        if (tokens.isNotEmpty()) {
                            TokensList(
                                tokens,
                                onEdit = { navController.navigate(RouteBuilder.tokenSetup(tokenId = it.id)) })
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

                    is HomeViewModel.UIState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Unable to load")
                            //TODO: display error
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
            val authUrl = extras.getString(SCAN_RESULT)
            if (Utils.isValidTOTPAuthURL(authUrl)) {
                navController.navigate(RouteBuilder.tokenSetup(authUrl = authUrl))
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