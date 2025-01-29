package com.boxy.authenticator.navigation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.boxy.authenticator.navigation.components.RootComponent.Configuration
import com.boxy.authenticator.ui.viewmodels.AuthenticationViewModel
import dev.icerock.moko.biometry.BiometryAuthenticator
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class AuthenticationScreenComponent(
    componentContext: ComponentContext,
    val navigation: StackNavigation<Configuration>,
) : ComponentContext by componentContext, KoinComponent {

    lateinit var authenticationViewModel: AuthenticationViewModel

    fun init(biometryAuthenticator: BiometryAuthenticator) {
        authenticationViewModel = get<AuthenticationViewModel> {
            parametersOf(biometryAuthenticator)
        }
    }

    fun navigateToHome() {
        navigation.replaceCurrent(Configuration.HomeScreen)
    }
}
