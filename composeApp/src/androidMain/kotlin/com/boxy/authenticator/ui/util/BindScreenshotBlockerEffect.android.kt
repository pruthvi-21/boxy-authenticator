package com.boxy.authenticator.ui.util

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.boxy.authenticator.MainActivity

@Composable
actual fun BindScreenshotBlockerEffect(enabled: Boolean) {
    val context = LocalContext.current

    if (context is MainActivity) {
        if (enabled) {
            context.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            context.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}