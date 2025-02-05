package com.boxy.authenticator.navigation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.ui.viewmodels.TokenSetupViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class TokenSetupScreenComponent(
    componentContext: ComponentContext,
    val navigation: StackNavigation<RootComponent.Configuration>,
    val token: TokenEntry? = null,
    val authUrl: String? = null,
) : ComponentContext by componentContext, KoinComponent {

    val viewModel: TokenSetupViewModel by lazy { get<TokenSetupViewModel>() }

    fun navigateUp(userClickEvent: Boolean = false) {
        if (userClickEvent) {
            if (viewModel.isFormUpdated()) {
                viewModel.showBackPressDialog.value = true
                return
            }
        }
        navigation.pop()
    }

}