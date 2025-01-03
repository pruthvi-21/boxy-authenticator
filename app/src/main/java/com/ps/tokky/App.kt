package com.ps.tokky

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ps.tokky.helpers.TransitionHelper
import com.ps.tokky.navigation.Routes
import com.ps.tokky.navigation.addAuthRoute
import com.ps.tokky.navigation.addExportTokensRoute
import com.ps.tokky.navigation.addHomeRoute
import com.ps.tokky.navigation.addImportTokensRoute
import com.ps.tokky.navigation.addSettingsRoute
import com.ps.tokky.navigation.addTokenSetupRoute
import com.ps.tokky.ui.theme.TokkyTheme
import com.ps.tokky.ui.viewmodels.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext

@Composable
fun App() {
    val context = LocalContext.current
    val transitions = TransitionHelper(context)

    val settingsViewModel: SettingsViewModel = koinViewModel()
    val isAppLockEnabled by remember { mutableStateOf(settingsViewModel.isAppLockEnabled.value) }

    KoinContext {
        TokkyTheme(theme = settingsViewModel.appTheme.value) {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = if (isAppLockEnabled) Routes.Auth.base else Routes.Home.base,
                enterTransition = { transitions.screenEnterAnim },
                exitTransition = { transitions.screenExitAnim },
                popEnterTransition = { transitions.screenPopEnterAnim },
                popExitTransition = { transitions.screenPopExitAnim }
            ) {
                addAuthRoute(navController)
                addHomeRoute(navController)
                addTokenSetupRoute(navController)
                addSettingsRoute(settingsViewModel, navController)
                addExportTokensRoute()
                addImportTokensRoute(navController)
            }
        }
    }
}