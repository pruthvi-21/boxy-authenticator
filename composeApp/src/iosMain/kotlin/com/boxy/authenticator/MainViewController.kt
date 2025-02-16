package com.boxy.authenticator

import androidx.compose.ui.window.ComposeUIViewController
import com.boxy.authenticator.di.platformModule
import com.boxy.authenticator.di.sharedModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(sharedModule, platformModule)
        }
    }
) {
    App()
}
