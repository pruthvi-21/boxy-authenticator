package com.boxy.authenticator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.boxy.authenticator.navigation.RootComponent
import com.boxy.authenticator.navigation.RootComponent.Child
import com.boxy.authenticator.navigation.backAnimation
import com.boxy.authenticator.ui.screens.HomeScreen
import com.boxy.authenticator.ui.screens.SettingsScreen
import com.boxy.authenticator.ui.screens.TokenSetupScreen
import com.boxy.authenticator.ui.theme.BoxyTheme
import com.boxy.authenticator.ui.util.BindScreenshotBlockerEffect
import com.boxy.authenticator.ui.viewmodels.SettingsViewModel
import dev.icerock.moko.biometry.compose.BindBiometryAuthenticatorEffect
import dev.icerock.moko.biometry.compose.rememberBiometryAuthenticatorFactory
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.ParametersHolder

@Composable
fun App(rootComponent: RootComponent) {
    val biometryFactory = rememberBiometryAuthenticatorFactory()
    val settingsViewModel: SettingsViewModel = koinViewModel {
        ParametersHolder(mutableListOf(biometryFactory.createBiometryAuthenticator()))
    }
    BindBiometryAuthenticatorEffect(settingsViewModel.biometryAuthenticator)
    BindScreenshotBlockerEffect(settingsViewModel.isBlockScreenshotsEnabled.value)

    KoinContext {
        BoxyTheme(
            theme = settingsViewModel.appTheme.value
        ) {
            val childStack by rootComponent.childStack.subscribeAsState()

            Children(
                stack = childStack,
                animation = backAnimation(
                    backHandler = rootComponent.backHandler,
                    onBack = rootComponent::onBackClicked,
                ),
            ) { child ->
                when (val instance = child.instance) {
                    is Child.HomeScreen -> HomeScreen(instance.component)
                    is Child.TokenSetupScreen -> TokenSetupScreen(instance.component)
                    is Child.SettingsScreen -> SettingsScreen(instance.component, settingsViewModel)
                }
            }
        }
    }
}