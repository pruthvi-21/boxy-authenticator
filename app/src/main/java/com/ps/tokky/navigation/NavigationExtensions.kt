package com.ps.tokky.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ps.tokky.ui.screens.HomeScreen
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
        route = "${Routes.TokenSetup.base}?token_id={token_id}",
        arguments = listOf(navArgument("token_id") {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        }),
    ) { navBackStackEntry ->
        val tokenId = navBackStackEntry.arguments?.getString("token_id")
        TokenSetupScreen(
            tokenId = tokenId,
            tokensViewModel = tokensViewModel,
            navController = navController,
        )
    }
}