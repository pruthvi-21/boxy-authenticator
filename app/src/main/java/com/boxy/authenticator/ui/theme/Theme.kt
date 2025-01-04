package com.boxy.authenticator.ui.theme

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.boxy.authenticator.utils.AppTheme

@Composable
fun TokkyTheme(
    theme: AppTheme = AppTheme.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    val lightColorScheme = buildLightColorScheme(context, dynamicColor)
    val darkColorScheme = buildDarkColorScheme(context, dynamicColor)

    val isDarkTheme = when (theme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDarkTheme) darkColorScheme else lightColorScheme

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

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = TokkyShapes,
        content = content
    )
}

private fun buildLightColorScheme(context: Context, dynamicColor: Boolean): ColorScheme {
    val lightScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && dynamicColor)
        dynamicLightColorScheme(context)
    else lightColorScheme()

    return lightScheme.copy(
        surfaceVariant = lightScheme.surfaceContainer,
        outlineVariant = lightScheme.outlineVariant.copy(0.7f)
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
        outlineVariant = darkScheme.outlineVariant.copy(0.5f),
    )
}

fun Color.mixWith(other: Color, ratio: Float): Color {
    return lerp(this, other, ratio.coerceIn(0f, 1f))
}