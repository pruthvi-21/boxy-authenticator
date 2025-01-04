package com.boxy.authenticator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.boxy.authenticator.helpers.TransitionHelper
import com.boxy.authenticator.navigation.Routes
import com.boxy.authenticator.navigation.addAuthRoute
import com.boxy.authenticator.navigation.addExportTokensRoute
import com.boxy.authenticator.navigation.addHomeRoute
import com.boxy.authenticator.navigation.addImportTokensRoute
import com.boxy.authenticator.navigation.addSettingsRoute
import com.boxy.authenticator.navigation.addTokenSetupRoute
import com.boxy.design.theme.BoxyTheme
import com.boxy.authenticator.ui.viewmodels.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext

@Composable
fun App() {
    val context = LocalContext.current
    val transitions = TransitionHelper(context)

    val settingsViewModel: SettingsViewModel = koinViewModel()
    val isAppLockEnabled by remember { mutableStateOf(settingsViewModel.isAppLockEnabled.value) }

    KoinContext {
        BoxyTheme(theme = settingsViewModel.appTheme.value) {
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