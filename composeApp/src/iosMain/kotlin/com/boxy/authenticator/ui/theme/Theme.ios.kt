package com.boxy.authenticator.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun buildPlatformLightColorScheme(dynamicColor: Boolean): ColorScheme {
    return LightColorScheme
}

@Composable
actual fun buildPlatformDarkColorScheme(dynamicColor: Boolean): ColorScheme {
    return DarkColorScheme
}

@Composable
actual fun UpdatePlatformTheme(colorScheme: ColorScheme, isDarkTheme: Boolean) {
    // Not Required
}