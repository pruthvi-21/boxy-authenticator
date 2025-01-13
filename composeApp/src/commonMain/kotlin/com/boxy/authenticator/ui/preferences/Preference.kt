package com.boxy.authenticator.ui.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.boxy.authenticator.ui.preferences.utils.PreferenceDefaults

@Composable
fun Preference(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    widgetContainer: @Composable (() -> Unit)? = null,
    showDivider: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    val theme = LocalPreferenceTheme.current
    BasicPreference(
        textContainer = {
            Column(
                modifier = Modifier.padding(theme.padding)
            ) {
                PreferenceDefaults.TitleContainer(title = title, enabled = enabled)
                PreferenceDefaults.SummaryContainer(summary = summary, enabled = enabled)
            }
        },
        modifier = modifier,
        enabled = enabled,
        iconContainer = { PreferenceDefaults.IconContainer(icon = icon, enabled = enabled) },
        widgetContainer = { widgetContainer?.invoke() },
        showDivider = showDivider,
        onClick = onClick,
    )
}