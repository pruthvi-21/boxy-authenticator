package com.boxy.authenticator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.boxy.authenticator.core.AppSettings
import org.koin.compose.koinInject

@Composable
fun BoxyNavHost(
    builder: NavGraphBuilder.() -> Unit,
) {
    val density = LocalDensity.current
    val transitions = TransitionHelper(density)

    val settings: AppSettings = koinInject()
    val navController = rememberNavController()

    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            navController = navController,
            startDestination = if (settings.isAppLockEnabled()) Routes.Auth.base
            else Routes.Home.base,
            builder = builder,
            enterTransition = { transitions.screenEnterAnim },
            exitTransition = { transitions.screenExitAnim },
            popEnterTransition = { transitions.screenPopEnterAnim },
            popExitTransition = { transitions.screenPopExitAnim },
        )
    }
}