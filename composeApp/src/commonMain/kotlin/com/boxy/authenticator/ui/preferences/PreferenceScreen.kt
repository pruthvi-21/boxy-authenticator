package com.boxy.authenticator.ui.preferences

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceScreen(
    theme: PreferenceTheme = PreferenceTheme.Default,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit,
) {
    CompositionLocalProvider(LocalPreferenceTheme provides theme) {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(bottom = 50.dp),
            content = content
        )
    }
}