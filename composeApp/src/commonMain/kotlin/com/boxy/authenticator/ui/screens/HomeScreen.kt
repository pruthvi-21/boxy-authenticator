package com.boxy.authenticator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.app_name
import boxy_authenticator.composeapp.generated.resources.empty_layout_text
import boxy_authenticator.composeapp.generated.resources.expandable_fab_manual_title
import boxy_authenticator.composeapp.generated.resources.expandable_fab_qr_title
import boxy_authenticator.composeapp.generated.resources.title_settings
import com.boxy.authenticator.navigation.HomeScreenComponent
import com.boxy.authenticator.ui.components.ExpandableFab
import com.boxy.authenticator.ui.components.ExpandableFabItem
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.screens.home.TokensList
import com.boxy.authenticator.ui.viewmodels.HomeViewModel
import com.boxy.authenticator.utils.copy
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(component: HomeScreenComponent) {
    val homeViewModel = component.homeViewModel

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
                    title = stringResource(Res.string.app_name),
                    showDefaultNavigationIcon = false,
                    actions = {
                        IconButton(onClick = {
                            component.navigateToSettings()
                        }) {
                            Icon(
                                imageVector = Icons.TwoTone.Settings,
                                contentDescription = stringResource(Res.string.title_settings),
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
                                onEdit = { component.navigateToTokenSetupScreen(tokenId = it.id) })
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(Res.string.empty_layout_text),
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

            val items = listOf(
                ExpandableFabItem(stringResource(Res.string.expandable_fab_qr_title), Icons.Outlined.QrCodeScanner),
                ExpandableFabItem(stringResource(Res.string.expandable_fab_manual_title), Icons.Outlined.Edit),
            )

            ExpandableFab(
                items = items,
                onItemClick = { index ->
                    homeViewModel.toggleFabState(false)
                    when (index) {
                        0 -> component.navigateToQrScannerScreen()
                        1 -> component.navigateToTokenSetupScreen()
                    }
                },
                modifier = Modifier.padding(safePadding),
            )
        }
    }
}