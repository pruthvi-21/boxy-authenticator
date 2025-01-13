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
import com.boxy.authenticator.navigation.DefaultRootComponent
import org.koin.core.context.startKoin

@OptIn(ExperimentalDecomposeApi::class)
fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(sharedModule, platformModule)
        }
    }
) {
    val backDispatcher = remember { BackDispatcher() }
    val rootComponent = remember {
        DefaultRootComponent(
            componentContext = DefaultComponentContext(LifecycleRegistry()),
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
