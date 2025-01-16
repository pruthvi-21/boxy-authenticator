package com.jw.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceCategory(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
    Column {
        BasicPreference(
            textContainer = {
                val theme = LocalPreferenceTheme.current
                Column {
                    Box(
                        modifier = Modifier.padding(theme.categoryPadding),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        CompositionLocalProvider(LocalContentColor provides theme.categoryTitleColor) {
                            ProvideTextStyle(value = theme.categoryTitleStyle, content = title)
                        }
                    }
                    Column(
                        modifier = Modifier
                            .clip(theme.categoryContentShape)
                            .background(theme.preferenceColor)
                    ) {
                        content?.invoke()
                        if (theme.showCategoryDivider) {
                            HorizontalDivider(
                                color = theme.dividerColor,
                                thickness = theme.dividerThickness,
                            )
                        }
                    }
                }
            },
            showDivider = false,
            modifier = modifier.padding(bottom = 16.dp),
        )
    }
}