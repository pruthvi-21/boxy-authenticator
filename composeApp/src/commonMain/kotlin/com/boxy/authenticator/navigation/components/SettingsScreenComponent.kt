package com.boxy.authenticator.navigation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.boxy.authenticator.domain.models.TokenEntry

class SettingsScreenComponent(
    componentContext: ComponentContext,
    val hideSensitiveSettings: Boolean,
    val navigation: StackNavigation<RootComponent.Configuration>,
) : ComponentContext by componentContext {

    fun navigateToExport() {
        navigation.pushNew(RootComponent.Configuration.ExportTokensScreen)
    }

    fun navigateToImport(tokens: List<TokenEntry>) {
        navigation.pushNew(RootComponent.Configuration.ImportTokensScreen(tokens))
    }

    fun navigateUp() {
        navigation.pop()
    }
}