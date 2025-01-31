package com.boxy.authenticator.navigation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.boxy.authenticator.navigation.components.RootComponent.Configuration
import org.koin.core.component.KoinComponent

class QrScannerScreenComponent(
    componentContext: ComponentContext,
    val navigation: StackNavigation<Configuration>,
) : ComponentContext by componentContext, KoinComponent {

    fun navigateToTokenSetupScreen(authUrl: String? = null) {
        navigation.replaceCurrent(Configuration.TokenSetupScreen(authUrl = authUrl))
    }

    fun navigateUp() {
        navigation.pop()
    }
}