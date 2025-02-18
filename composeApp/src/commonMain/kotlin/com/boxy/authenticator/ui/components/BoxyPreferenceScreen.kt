package com.boxy.authenticator.ui.components

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jw.preferences.PreferenceScreen
import com.jw.preferences.PreferenceTheme

@Composable
fun BoxyPreferenceScreen(
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit,
) {
    PreferenceScreen(
        theme = PreferenceTheme.Default.copy(
            preferenceColor = MaterialTheme.colorScheme.surfaceVariant,
            showPreferenceDivider = true,
            categoryContentShape = MaterialTheme.shapes.medium,
            showCategoryDivider = false,
            customSwitch = { checked, enabled ->
                BoxSwitch(checked = checked, onCheckedChange = null, enabled = enabled)
            }
        ),
        modifier = modifier,
        content = content,
    )
}