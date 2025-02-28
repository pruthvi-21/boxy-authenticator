package com.boxy.authenticator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.app_name
import boxy_authenticator.composeapp.generated.resources.dismiss
import boxy_authenticator.composeapp.generated.resources.empty_layout_text
import boxy_authenticator.composeapp.generated.resources.expandable_fab_manual_title
import boxy_authenticator.composeapp.generated.resources.expandable_fab_qr_title
import boxy_authenticator.composeapp.generated.resources.no_backup_taken_msg
import boxy_authenticator.composeapp.generated.resources.outdated_backup_msg
import boxy_authenticator.composeapp.generated.resources.title_settings
import com.boxy.authenticator.core.Platform
import com.boxy.authenticator.navigation.LocalNavController
import com.boxy.authenticator.navigation.navigateToEditTokenScreen
import com.boxy.authenticator.navigation.navigateToNewTokenSetupScreen
import com.boxy.authenticator.navigation.navigateToQrScannerScreen
import com.boxy.authenticator.navigation.navigateToSettings
import com.boxy.authenticator.ui.components.ExpandableFab
import com.boxy.authenticator.ui.components.ExpandableFabItem
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.components.design.BoxyScaffold
import com.boxy.authenticator.ui.screens.home.TokensList
import com.boxy.authenticator.ui.util.SystemBackHandler
import com.boxy.authenticator.ui.viewmodels.HomeViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun HomeScreen() {

    val navController = LocalNavController.current
    val homeViewModel: HomeViewModel = koinViewModel()

    val tokensState by homeViewModel.tokensState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        homeViewModel.loadTokens()
        if ((homeViewModel.isLastBackupOutdated || !homeViewModel.hasTakenAtleastOneBackup) &&
            !homeViewModel.isSnackBarDismissed
        ) {
            coroutineScope.launch {
                val message = when {
                    !homeViewModel.hasTakenAtleastOneBackup -> getString(Res.string.no_backup_taken_msg)
                    homeViewModel.isLastBackupOutdated -> getString(Res.string.outdated_backup_msg)
                    else -> null
                }

                if (message != null) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = message,
                            actionLabel = getString(Res.string.dismiss),
                        )
                        homeViewModel.dismissSnackbar()
                    }
                }
            }
        }
    }

    SystemBackHandler(enabled = homeViewModel.isFabExpanded) {
        homeViewModel.setIsFabExpanded(false)
    }

    BoxyScaffold(
        topBar = {
            Toolbar(
                title = stringResource(Res.string.app_name),
                showDefaultNavigationIcon = false,
                actions = {
                    IconButton(onClick = { navController.navigateToSettings() }) {
                        Icon(
                            imageVector = Icons.TwoTone.Settings,
                            contentDescription = stringResource(Res.string.title_settings),
                        )
                    }
                },
            )
        }
    ) { safePadding ->
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(safePadding)
            ) {
                when (val uiState = tokensState) {
                    is HomeViewModel.UIState.Loading -> {}

                    is HomeViewModel.UIState.Success -> {
                        val tokens = uiState.data

                        if (tokens.isNotEmpty()) {
                            TokensList(
                                tokens,
                                onEdit = {
                                    navController.navigateToEditTokenScreen(tokenId = it.id)
                                }
                            )
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
        }
    }

    if (Platform.isAndroid) {
        // Not needed in IOS as action sheet is used
        AnimatedVisibility(
            visible = homeViewModel.isFabExpanded,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .then(
                        if (!homeViewModel.isFabExpanded) Modifier
                        else Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = homeViewModel.isFabExpanded
                        ) { homeViewModel.setIsFabExpanded(false) }
                    )
            )
        }
    }

    val items = listOf(
        ExpandableFabItem(
            stringResource(Res.string.expandable_fab_qr_title),
            Icons.Outlined.QrCodeScanner
        ),
        ExpandableFabItem(
            stringResource(Res.string.expandable_fab_manual_title),
            Icons.Outlined.Edit
        ),
    )

    ExpandableFab(
        isFabExpanded = homeViewModel.isFabExpanded,
        items = items,
        onItemClick = { index ->
            homeViewModel.setIsFabExpanded(false)
            when (index) {
                0 -> navController.navigateToQrScannerScreen()
                1 -> navController.navigateToNewTokenSetupScreen()
            }
        },
        onFabExpandChange = {
            homeViewModel.setIsFabExpanded(it)
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(16.dp),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .padding(WindowInsets.safeDrawing.asPaddingValues())
                .align(Alignment.BottomCenter),
        ) { snackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                actionColor = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    }

}