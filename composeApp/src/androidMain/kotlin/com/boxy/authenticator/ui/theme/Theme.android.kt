package com.boxy.authenticator.ui.theme

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
actual fun buildPlatformLightColorScheme(dynamicColor: Boolean): ColorScheme {
    return if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        dynamicLightColorScheme(LocalContext.current)
    else LightColorScheme
}

@Composable
actual fun buildPlatformDarkColorScheme(dynamicColor: Boolean): ColorScheme {
    return if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        dynamicDarkColorScheme(LocalContext.current)
    else DarkColorScheme
}

@Composable
actual fun UpdatePlatformTheme(colorScheme: ColorScheme, isDarkTheme: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !isDarkTheme
            insetsController.isAppearanceLightNavigationBars = !isDarkTheme
            window.setBackgroundDrawable(
                ColorDrawable(colorScheme.surface.value.toInt()),
            )
        }
    }
}