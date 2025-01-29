package com.boxy.authenticator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureOverlay
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.boxy.authenticator.di.platformModule
import com.boxy.authenticator.di.sharedModule
import com.boxy.authenticator.helpers.AppSettings
import com.boxy.authenticator.navigation.components.DefaultRootComponent
import com.boxy.authenticator.navigation.components.RootComponent
import org.koin.compose.koinInject
import org.koin.core.context.startKoin

@OptIn(ExperimentalDecomposeApi::class)
fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(sharedModule, platformModule)
        }
    }
) {
    val appSettings = koinInject<AppSettings>()
    val backDispatcher = remember { BackDispatcher() }

    val rootComponent = remember {
        DefaultRootComponent(
            componentContext = DefaultComponentContext(LifecycleRegistry()),
            initialConfiguration =
            if (appSettings.isAppLockEnabled()) RootComponent.Configuration.AuthenticationScreen
            else RootComponent.Configuration.HomeScreen,
            backHandler = backDispatcher
        )
    }

    PredictiveBackGestureOverlay(
        backDispatcher = backDispatcher,
        backIcon = { _, _ -> },
        modifier = Modifier.fillMaxSize(),
    ) {
        App(rootComponent = rootComponent)
    }
}
