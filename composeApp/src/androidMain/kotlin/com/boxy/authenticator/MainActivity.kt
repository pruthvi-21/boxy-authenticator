package com.boxy.authenticator

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.decompose.retainedComponent
import com.boxy.authenticator.core.AppSettings
import com.boxy.authenticator.navigation.components.DefaultRootComponent
import com.boxy.authenticator.navigation.components.RootComponent
import io.github.vinceglb.filekit.core.FileKit
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val appSettings by inject<AppSettings>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        val rootComponent = retainedComponent {
            DefaultRootComponent(
                componentContext = it,
                initialConfiguration =
                if (appSettings.isAppLockEnabled()) RootComponent.Configuration.AuthenticationScreen
                else RootComponent.Configuration.HomeScreen
            )
        }

        setContent {
            App(rootComponent)
        }

        FileKit.init(this)
    }
}
