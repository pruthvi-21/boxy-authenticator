package com.boxy.authenticator.navigation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushNew
import com.boxy.authenticator.navigation.components.RootComponent.Configuration
import com.boxy.authenticator.ui.viewmodels.HomeViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class HomeScreenComponent(
    componentContext: ComponentContext,
    val navigation: StackNavigation<Configuration>,
) : ComponentContext by componentContext, KoinComponent {

    val homeViewModel: HomeViewModel by lazy { get<HomeViewModel>() }

    fun navigateToTokenSetupScreen(tokenId: String? = null, authUrl: String? = null) {
        navigation.pushNew(Configuration.TokenSetupScreen(tokenId, authUrl))
    }

    fun navigateToSettings() {
        navigation.pushNew(Configuration.SettingsScreen())
    }

    fun navigateToQrScannerScreen() {
        navigation.pushNew(Configuration.QrScannerScreen)
    }
}