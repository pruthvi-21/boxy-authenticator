package com.boxy.authenticator

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.decompose.retainedComponent
import com.boxy.authenticator.navigation.DefaultRootComponent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        val rootComponent = retainedComponent {
            DefaultRootComponent(
                componentContext = it,
            )
        }

        setContent {
            App(rootComponent)
        }
    }
}
