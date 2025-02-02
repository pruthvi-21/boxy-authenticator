package com.boxy.authenticator.navigation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.boxy.authenticator.data.models.TokenEntry
import com.boxy.authenticator.navigation.components.RootComponent.Configuration
import com.boxy.authenticator.ui.viewmodels.ImportTokensViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ImportTokensScreenComponent(
    componentContext: ComponentContext,
    val tokens: List<TokenEntry>,
    val navigation: StackNavigation<Configuration>,
) : ComponentContext by componentContext, KoinComponent {

    val importTokensViewModel: ImportTokensViewModel by lazy { get<ImportTokensViewModel>() }

    fun navigateUp() {
        navigation.pop()
    }
}