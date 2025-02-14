package com.boxy.authenticator.navigation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.boxy.authenticator.navigation.components.RootComponent.Configuration
import com.boxy.authenticator.ui.viewmodels.ExportTokensViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ExportTokensScreenComponent(
    componentContext: ComponentContext,
    val navigation: StackNavigation<Configuration>,
) : ComponentContext by componentContext, KoinComponent {

    val exportTokensViewModel: ExportTokensViewModel by lazy { get<ExportTokensViewModel>() }

    fun navigateUp() { navigation.pop() }
}