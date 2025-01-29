package com.boxy.authenticator.navigation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandler
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.boxy.authenticator.navigation.components.RootComponent.Child
import com.boxy.authenticator.navigation.components.RootComponent.Configuration
import kotlinx.serialization.Serializable

interface RootComponent : BackHandlerOwner {

    @Serializable
    sealed class Configuration {
        @Serializable
        data object AuthenticationScreen : Configuration()

        @Serializable
        data object HomeScreen : Configuration()

        @Serializable
        data object QrScannerScreen : Configuration()

        @Serializable
        data class TokenSetupScreen(val tokenId: String? = null, val authUrl: String? = null) :
            Configuration()

        @Serializable
        data class SettingsScreen(val hideSensitiveSettings: Boolean = false) : Configuration()
    }

    sealed class Child {
        data class AuthenticationScreen(val component: AuthenticationScreenComponent) : Child()
        data class HomeScreen(val component: HomeScreenComponent) : Child()
        data class QrScannerScreen(val component: QrScannerScreenComponent) : Child()
        data class TokenSetupScreen(val component: TokenSetupScreenComponent) : Child()
        data class SettingsScreen(val component: SettingsScreenComponent) : Child()
    }

    val childStack: Value<ChildStack<Configuration, Child>>

    fun onBackClicked()
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    initialConfiguration: Configuration,
    override val backHandler: BackHandler = componentContext.backHandler,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Configuration>()

    override val childStack = childStack(
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
            is Configuration.AuthenticationScreen -> Child.AuthenticationScreen(
                AuthenticationScreenComponent(context, navigation)
            )

            is Configuration.HomeScreen -> Child.HomeScreen(
                HomeScreenComponent(context, navigation)
            )

            is Configuration.QrScannerScreen -> Child.QrScannerScreen(
                QrScannerScreenComponent(context, navigation)
            )

            is Configuration.TokenSetupScreen -> Child.TokenSetupScreen(
                TokenSetupScreenComponent(context, navigation, config.tokenId, config.authUrl)
            )

            is Configuration.SettingsScreen -> Child.SettingsScreen(
                SettingsScreenComponent(context, config.hideSensitiveSettings, navigation)
            )
        }
    }

    override fun onBackClicked() {
        navigation.pop()
    }
}
