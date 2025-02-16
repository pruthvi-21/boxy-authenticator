package com.boxy.authenticator.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.boxy.authenticator.core.serialization.BoxyJson
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.ui.screens.AuthenticationScreen
import com.boxy.authenticator.ui.screens.ExportTokensScreen
import com.boxy.authenticator.ui.screens.HomeScreen
import com.boxy.authenticator.ui.screens.ImportTokensScreen
import com.boxy.authenticator.ui.screens.QrScannerScreen
import com.boxy.authenticator.ui.screens.SettingsScreen
import com.boxy.authenticator.ui.screens.TokenSetupScreen
import io.ktor.http.decodeURLQueryComponent

fun NavGraphBuilder.addAuthRoute() {
    composable(Routes.Auth.base) {
        AuthenticationScreen()
    }
}

fun NavGraphBuilder.addHomeRoute() {
    composable(Routes.Home.base) {
        HomeScreen()
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

        TokenSetupScreen(
            tokenId = tokenId,
            authUrl = authUrl?.decodeURLQueryComponent(),
        )
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
        ExportTokensScreen()
    }
}

fun NavGraphBuilder.addImportTokensRoute() {
    composable(
        route = "${Routes.ImportTokens.base}?tokens={tokens}",
        arguments = listOf(
            navArgument("tokens") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { navBackStackEntry ->
        val content = navBackStackEntry.arguments?.getString("tokens") ?: ""

        val data = BoxyJson.decodeFromString<List<TokenEntry>>(content)
        ImportTokensScreen(data)
    }
}