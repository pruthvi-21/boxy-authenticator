package com.boxy.authenticator.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext,
    initialConfiguration: Configuration,
) : BackHandlerOwner, ComponentContext by componentContext {

    @Serializable
    sealed class Configuration {
        @Serializable
        data object HomeScreen : Configuration()

        @Serializable
        data class TokenSetupScreen(val tokenId: String? = null, val authUrl: String? = null) :
            Configuration()

        @Serializable
        data object SettingsScreen : Configuration()
    }

    sealed class Child {
        data class HomeScreen(val component: HomeScreenComponent) : Child()
        data class TokenSetupScreen(val component: TokenSetupScreenComponent) : Child()
        data class SettingsScreen(val component: SettingsScreenComponent) : Child()
    }

    private val navigation = StackNavigation<Configuration>()
    val childStack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = initialConfiguration,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(
        config: Configuration,
        context: ComponentContext,
    ): Child {
        return when (config) {
            is Configuration.HomeScreen -> Child.HomeScreen(
                HomeScreenComponent(context, navigation)
            )

            is Configuration.TokenSetupScreen -> Child.TokenSetupScreen(
                TokenSetupScreenComponent(context, navigation, config.tokenId, config.authUrl)
            )

            is Configuration.SettingsScreen -> Child.SettingsScreen(
                SettingsScreenComponent(context, navigation)
            )
        }
    }

    fun onBackClicked() {
        navigation.pop()
    }
}