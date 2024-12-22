package com.ps.tokky.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ps.tokky.ui.screens.HomeScreen
import com.ps.tokky.ui.screens.SettingsScreen
import com.ps.tokky.ui.screens.TokenSetupScreen
import com.ps.tokky.ui.viewmodels.TokensViewModel

fun NavGraphBuilder.addHomeRoute(
    navController: NavController,
    tokensViewModel: TokensViewModel
) {
    composable(Routes.Home.base) {
        HomeScreen(
            tokensViewModel = tokensViewModel,
            navController = navController,
        )
    }
}

fun NavGraphBuilder.addTokenSetupRoute(
    navController: NavController,
    tokensViewModel: TokensViewModel
) {
    composable(
        route = "${Routes.TokenSetup.base}?token_id={token_id}&auth_url={auth_url}",
        arguments = listOf(
            navArgument("token_id") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument("auth_url") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { navBackStackEntry ->
        val tokenId = navBackStackEntry.arguments?.getString("token_id")
        val authUrl = navBackStackEntry.arguments?.getString("auth_url")?.let { Uri.decode(it) }
        TokenSetupScreen(
            tokenId = tokenId,
            authUrl = authUrl,
            tokensViewModel = tokensViewModel,
            navController = navController
        )
    }
}

fun NavGraphBuilder.addSettingsRoute(
    navController: NavController,
) {
    composable(Routes.Settings.base) {
        SettingsScreen(
            navController = navController,
        )
    }
}