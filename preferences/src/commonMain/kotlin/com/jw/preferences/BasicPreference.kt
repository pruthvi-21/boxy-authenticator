package com.jw.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jw.preferences.utils.copy

@Composable
internal fun BasicPreference(
    textContainer: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showDivider: Boolean = true,
    iconContainer: @Composable () -> Unit = {},
    widgetContainer: @Composable () -> Unit = {},
    onClick: (() -> Unit)? = null,
) {
    val theme = LocalPreferenceTheme.current
    Column {
        Row(
            modifier =
            modifier.then(
                if (onClick != null) {
                    Modifier.clickable(enabled, onClick = onClick)
                } else {
                    Modifier
                }
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            iconContainer()
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.weight(1f)) { textContainer() }
                    widgetContainer()
                }
                if (showDivider && theme.addPaddingToDivider) {
                    HorizontalDivider(
                        color = theme.dividerColor,
                        thickness = theme.dividerThickness,
                        modifier = Modifier.padding(theme.preferencePadding.copy(top = 0.dp, bottom = 0.dp))
                    )
                }
            }
        }

        if (showDivider && !theme.addPaddingToDivider) {
            HorizontalDivider(
                color = theme.dividerColor,
                thickness = theme.dividerThickness,
            )
        }
    }
}