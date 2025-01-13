package com.boxy.authenticator

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.boxy.authenticator.di.platformModule
import com.boxy.authenticator.di.sharedModule
import com.boxy.authenticator.navigation.RootComponent
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(sharedModule, platformModule)
        }
    }
) {
    val rootComponent = remember {
        RootComponent(
            DefaultComponentContext(LifecycleRegistry()),
            initialConfiguration = RootComponent.Configuration.HomeScreen
        )
    }

    App(rootComponent)
}