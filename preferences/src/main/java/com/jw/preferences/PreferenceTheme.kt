package com.jw.preferences

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class PreferenceTheme(
    val categoryPadding: PaddingValues,
    val categoryColor: Color,
    val categoryTextStyle: TextStyle,
    val categoryShape: Shape,
    val preferenceColor: Color,
    val padding: PaddingValues,
    val horizontalSpacing: Dp,
    val verticalSpacing: Dp,
    val disabledOpacity: Float,
    val iconContainerMinWidth: Dp,
    val iconColor: Color,
    val titleColor: Color,
    val titleTextStyle: TextStyle,
    val summaryColor: Color,
    val summaryTextStyle: TextStyle,
    val dividerThickness: Dp,
    val dividerColor: Color,
    val addPaddingToDivider: Boolean,
) {
    companion object {
        val Default: PreferenceTheme
            @Composable
            get() = PreferenceTheme(
                categoryPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                categoryColor = MaterialTheme.colorScheme.primary,
                categoryTextStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                categoryShape = RoundedCornerShape(28.dp),
                preferenceColor = MaterialTheme.colorScheme.surfaceVariant,
                padding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                horizontalSpacing = 16.dp,
                verticalSpacing = 16.dp,
                disabledOpacity = 0.38f,
                iconContainerMinWidth = 56.dp,
                iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                titleColor = MaterialTheme.colorScheme.onSurface,
                titleTextStyle = MaterialTheme.typography.bodyLarge,
                summaryColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                summaryTextStyle = MaterialTheme.typography.bodyMedium,
                dividerThickness = 1.dp,
                dividerColor = MaterialTheme.colorScheme.outlineVariant,
                addPaddingToDivider = true,
            )
    }
}

val LocalPreferenceTheme = compositionLocalOf<PreferenceTheme> {
    error("No PreferenceTheme provided! Use PreferenceScreen to wrap your content.")
}
