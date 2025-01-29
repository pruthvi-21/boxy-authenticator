package com.boxy.authenticator.navigation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop

class SettingsScreenComponent(
    componentContext: ComponentContext,
    val navigation: StackNavigation<RootComponent.Configuration>,
) : ComponentContext by componentContext {

    fun navigateToExport() {
    }

    fun navigateToImport() {
    }

    fun navigateUp() {
        navigation.pop()
    }
}