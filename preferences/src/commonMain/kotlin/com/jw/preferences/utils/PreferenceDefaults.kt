package com.jw.preferences.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jw.preferences.LocalPreferenceTheme

internal object PreferenceDefaults {
    @Composable
    fun IconContainer(icon: @Composable (() -> Unit)?, enabled: Boolean) {
        if (icon != null) {
            val theme = LocalPreferenceTheme.current
            Box(
                modifier = Modifier
                    .widthIn(min = theme.iconContainerMinWidth)
                    .padding(theme.preferencePadding.copy(end = 0.dp)),
                contentAlignment = Alignment.CenterStart,
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides
                            theme.iconColor.let {
                                if (enabled) it else it.copy(alpha = theme.disabledOpacity)
                            },
                    content = icon,
                )
            }
        }
    }

    @Composable
    fun TitleContainer(title: @Composable () -> Unit, enabled: Boolean) {
        val theme = LocalPreferenceTheme.current
        CompositionLocalProvider(
            LocalContentColor provides
                    theme.titleColor.let { if (enabled) it else it.copy(alpha = theme.disabledOpacity) }
        ) {
            ProvideTextStyle(value = theme.titleTextStyle, content = title)
        }
    }

    @Composable
    fun SummaryContainer(summary: (@Composable () -> Unit)?, enabled: Boolean) {
        if (summary != null) {
            val theme = LocalPreferenceTheme.current
            CompositionLocalProvider(
                LocalContentColor provides
                        theme.summaryColor.let {
                            if (enabled) it else it.copy(alpha = theme.disabledOpacity)
                        }
            ) {
                ProvideTextStyle(value = theme.summaryTextStyle, content = summary)
            }
        }
    }
}