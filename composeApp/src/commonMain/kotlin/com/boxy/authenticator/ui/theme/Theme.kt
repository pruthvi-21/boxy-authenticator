package com.boxy.authenticator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.boxy.authenticator.domain.models.enums.AppTheme

val LightColorScheme = lightColorScheme()
val DarkColorScheme = darkColorScheme()

@Composable
fun BoxyTheme(
    theme: AppTheme = AppTheme.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val lightColorScheme = buildPlatformLightColorScheme(dynamicColor).applyLightThemeAdjustments()
    val darkColorScheme = buildPlatformDarkColorScheme(dynamicColor).applyDarkThemeAdjustments()

    val isDarkTheme = when (theme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDarkTheme) darkColorScheme else lightColorScheme

    UpdatePlatformTheme(colorScheme, isDarkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = TokkyShapes,
        content = content
    )
}

@Composable
expect fun UpdatePlatformTheme(colorScheme: ColorScheme, isDarkTheme: Boolean)

@Composable
expect fun buildPlatformLightColorScheme(dynamicColor: Boolean): ColorScheme

@Composable
expect fun buildPlatformDarkColorScheme(dynamicColor: Boolean): ColorScheme

private fun ColorScheme.applyLightThemeAdjustments(): ColorScheme = copy(
    surfaceVariant = surfaceContainer,
    outlineVariant = outlineVariant.copy(0.7f)
)

private fun ColorScheme.applyDarkThemeAdjustments(): ColorScheme = copy(
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = surface.mixWith(primary, .005f),
    outlineVariant = outlineVariant.copy(0.5f),
)

fun Color.mixWith(other: Color, ratio: Float): Color {
    return lerp(this, other, ratio.coerceIn(0f, 1f))
}