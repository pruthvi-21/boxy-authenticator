package com.ps.tokky

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ps.tokky.helpers.TransitionHelper
import com.ps.tokky.navigation.Routes
import com.ps.tokky.navigation.addExportTokensRoute
import com.ps.tokky.navigation.addHomeRoute
import com.ps.tokky.navigation.addSettingsRoute
import com.ps.tokky.navigation.addTokenSetupRoute
import com.ps.tokky.ui.theme.TokkyTheme
import com.ps.tokky.ui.viewmodels.TokensViewModel

@Composable
fun App() {
    val tokensViewModel: TokensViewModel = hiltViewModel()
    val transitions = TransitionHelper(LocalContext.current)

    TokkyTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Routes.Home.base,
            enterTransition = { transitions.screenEnterAnim },
            exitTransition = { transitions.screenExitAnim },
            popEnterTransition = { transitions.screenPopEnterAnim },
            popExitTransition = { transitions.screenPopExitAnim }
        ) {
            addHomeRoute(navController, tokensViewModel)
            addTokenSetupRoute(navController, tokensViewModel)
            addSettingsRoute(navController)
            addExportTokensRoute()
        }
    }
}