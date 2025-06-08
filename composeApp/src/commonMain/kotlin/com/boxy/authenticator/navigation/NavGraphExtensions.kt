package com.boxy.authenticator.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.boxy.authenticator.ui.screens.AuthenticationScreen
import com.boxy.authenticator.ui.screens.EditTokenScreen
import com.boxy.authenticator.ui.screens.ExportTokensScreen
import com.boxy.authenticator.ui.screens.HomeScreen
import com.boxy.authenticator.ui.screens.ImportTokensScreen
import com.boxy.authenticator.ui.screens.QrScannerScreen
import com.boxy.authenticator.ui.screens.SettingsScreen
import com.boxy.authenticator.ui.screens.TokenSetupFromUrlScreen
import com.boxy.authenticator.ui.screens.TokenSetupScreen
import com.boxy.authenticator.ui.viewmodels.AuthenticationViewModel
import com.boxy.authenticator.ui.viewmodels.ExportTokensViewModel
import com.boxy.authenticator.ui.viewmodels.HomeViewModel
import com.boxy.authenticator.ui.viewmodels.LocalSettingsViewModel
import io.ktor.http.decodeURLQueryComponent
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.ParametersHolder

fun NavGraphBuilder.addAuthRoute() {
    composable(Routes.Auth.base) {
        val navController = LocalNavController.current
        val settingsViewModel = LocalSettingsViewModel.current
        val authViewModel: AuthenticationViewModel = koinViewModel {
            ParametersHolder(mutableListOf(settingsViewModel.biometryAuthenticator))
        }

        val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

        val isBiometricUnlockEnabled = authViewModel.isBiometricUnlockEnabled()

        AuthenticationScreen(
            uiState = uiState,
            isBiometricUnlockEnabled = isBiometricUnlockEnabled,
            isPinPadVisible = authViewModel.isPinPadVisible.value,
            onPasswordChange = { authViewModel.updatePassword(it) },
            onSubmit = {
                authViewModel.verifyPassword {
                    if (it) navController.navigateToHome(true)
                }
            },
            promptForBiometrics = {
                authViewModel.promptForBiometrics {
                    if (it) navController.navigateToHome(true)
                }
            },
            updatePinPadVisibility = { authViewModel.updatePinPadVisibility() },
            onNavigateToSettings = { navController.navigateToSettings(hideSensitiveSettings = true) }
        )
    }
}

fun NavGraphBuilder.addHomeRoute() {
    composable(Routes.Home.base) {
        val navController = LocalNavController.current
        val homeViewModel: HomeViewModel = koinViewModel()

        val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

        HomeScreen(
            uiState = uiState,
            loadTokens = { homeViewModel.loadTokens() },
            onFabExpanded = { homeViewModel.setIsFabExpanded(it) },
            onDismissSnackbar = { homeViewModel.dismissSnackbar() },
            onNavigateToSettings = { navController.navigateToSettings() },
            onNavigateToQrScan = { navController.navigateToQrScannerScreen() },
            onNavigateToNewTokenSetup = { navController.navigateToNewTokenSetupScreen() },
            onNavigateToEditToken = { navController.navigateToEditTokenScreen(tokenId = it) }
        )
    }
}

fun NavGraphBuilder.addQrScannerRoute() {
    composable(Routes.QrScanner.base) {
        QrScannerScreen()
    }
}

fun NavGraphBuilder.addTokenSetupRoute() {
    composable(
        route = "${Routes.TokenSetup.base}?token_id={token_id}&auth_url={auth_url}",
    ) { navBackStackEntry ->
        val arguments = navBackStackEntry.arguments
        val tokenId = arguments?.getString("token_id")
        val authUrl = arguments?.getString("auth_url")

        when {
            authUrl != null -> TokenSetupFromUrlScreen(authUrl.decodeURLQueryComponent())
            tokenId != null -> EditTokenScreen(tokenId)
            else -> TokenSetupScreen()
        }
    }
}

fun NavGraphBuilder.addSettingsRoute() {
    composable(
        route = "${Routes.Settings.base}?hideSensitiveSettings={hideSensitiveSettings}",
        arguments = listOf(
            navArgument("hideSensitiveSettings") {
                type = NavType.BoolType
                nullable = false
                defaultValue = false
            },
        )
    ) { navBackStackEntry ->
        val hideSensitiveSettings =
            navBackStackEntry.arguments!!.getBoolean("hideSensitiveSettings")

        SettingsScreen(
            hideSensitiveSettings = hideSensitiveSettings,
        )
    }
}

fun NavGraphBuilder.addExportTokensRoute() {
    composable(Routes.ExportTokens.base) {
        val navController = LocalNavController.current
        val exportViewModel: ExportTokensViewModel = koinViewModel()

        val uiState by exportViewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            exportViewModel.loadAllTokens()
        }

        ExportTokensScreen(
            uiState = uiState,
            showPlainTextWarningDialog = { exportViewModel.showPlainTextWarningDialog(it) },
            showSetPasswordDialog = { exportViewModel.showSetPasswordDialog(it) },
            exportToPlainTextFile = { exportViewModel.exportToPlainTextFile(it) },
            exportToBoxyFile = { password, onDone ->
                exportViewModel.exportToBoxyFile(password, onDone)
            },
            onNavigateUp = { navController.navigateUp() }
        )
    }
}

fun NavGraphBuilder.addImportTokensRoute() {
    composable(Routes.ImportTokens.base) {
        ImportTokensScreen()
    }
}