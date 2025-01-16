package com.jw.preferences

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class PreferenceTheme(
    val preferenceColor: Color,
    val preferencePadding: PaddingValues,
    val titleColor: Color,
    val titleTextStyle: TextStyle,
    val summaryColor: Color,
    val summaryTextStyle: TextStyle,
    val showPreferenceDivider: Boolean,
    val categoryPadding: PaddingValues,
    val categoryTitleColor: Color,
    val categoryTitleStyle: TextStyle,
    val categoryContentShape: Shape,
    val showCategoryDivider: Boolean,
    val disabledOpacity: Float,
    val iconContainerMinWidth: Dp,
    val iconColor: Color,
    val customSwitch: @Composable ((checked: Boolean, enabled: Boolean) -> Unit)?,
    val dropdownShape: Shape,
    val dividerThickness: Dp,
    val dividerColor: Color,
    val addPaddingToDivider: Boolean,
) {
    companion object {
        val Default: PreferenceTheme
            @Composable
            get() = PreferenceTheme(
                preferenceColor = MaterialTheme.colorScheme.surface,
                preferencePadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                titleColor = MaterialTheme.colorScheme.onSurface,
                titleTextStyle = MaterialTheme.typography.bodyLarge,
                summaryColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                summaryTextStyle = MaterialTheme.typography.bodyMedium,
                showPreferenceDivider = false,
                categoryPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                categoryTitleColor = MaterialTheme.colorScheme.primary,
                categoryTitleStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                categoryContentShape = RectangleShape,
                showCategoryDivider = true,
                disabledOpacity = 0.38f,
                iconContainerMinWidth = 56.dp,
                iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                customSwitch = null,
                dropdownShape = RoundedCornerShape(2.dp),
                dividerThickness = 1.dp,
                dividerColor = MaterialTheme.colorScheme.outlineVariant,
                addPaddingToDivider = true,
            )
    }
}

val LocalPreferenceTheme = compositionLocalOf<PreferenceTheme> {
    error("No PreferenceTheme provided! Use PreferenceScreen to wrap your content.")
}
