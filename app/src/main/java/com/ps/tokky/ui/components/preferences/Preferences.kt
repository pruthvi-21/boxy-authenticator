package com.ps.tokky.ui.components.preferences

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private const val DisabledAlpha = 0.38f

@Composable
fun PreferenceTitle(
    text: String,
    disabled: Boolean = false,
    modifier: Modifier = Modifier
) {
    val alpha = if (disabled) DisabledAlpha else 1f
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
fun PreferenceSummary(
    text: String,
    disabled: Boolean = false,
    modifier: Modifier = Modifier
) {
    val alpha = if (disabled) DisabledAlpha else 0.75f
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
        maxLines = 10,
        modifier = modifier
    )
}