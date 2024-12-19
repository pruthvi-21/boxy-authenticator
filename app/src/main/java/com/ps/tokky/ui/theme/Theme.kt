package com.ps.tokky.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext

@Composable
fun TokkyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val lightColorScheme = buildLightColorScheme(context, dynamicColor)
    val darkColorScheme = buildDarkColorScheme(context, dynamicColor)

    val colorScheme = if (darkTheme) darkColorScheme else lightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

private fun buildLightColorScheme(context: Context, dynamicColor: Boolean): ColorScheme {
    val lightScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && dynamicColor)
        dynamicLightColorScheme(context)
    else lightColorScheme()

    return lightScheme.copy(
        surfaceVariant = lightScheme.surfaceContainer
    )
}

private fun buildDarkColorScheme(context: Context, dynamicColor: Boolean): ColorScheme {
    val darkScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && dynamicColor)
        dynamicDarkColorScheme(context)
    else darkColorScheme()

    return darkScheme.copy(
        background = Color.Black,
        surface = Color.Black,
        surfaceVariant = darkScheme.surface.mixWith(darkScheme.primary, .005f),
    )
}

val ColorScheme.surfaceVariant2: Color
    get() = outlineVariant.copy(alpha = 0.5f)

fun Color.mixWith(other: Color, ratio: Float): Color {
    return lerp(this, other, ratio.coerceIn(0f, 1f))
}