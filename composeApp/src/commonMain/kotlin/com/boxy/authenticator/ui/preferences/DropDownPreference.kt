package com.boxy.authenticator.ui.preferences

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.boxy.authenticator.ui.preferences.utils.copy

@Composable
fun <T> DropDownPreference(
    value: T,
    onValueChange: (T) -> Unit,
    entries: List<T>,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    valueToText: (T) -> AnnotatedString = { AnnotatedString(it.toString()) },
    item: @Composable (value: T, currentValue: T, onClick: () -> Unit) -> Unit =
        ListPreferenceDefaults.item(valueToText),
    showDivider: Boolean = true,
) {
    var openSelector by rememberSaveable { mutableStateOf(false) }
    // Put DropdownMenu before Preference so that it can anchor to the right position.
    if (openSelector) {
        val theme = LocalPreferenceTheme.current
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(theme.padding.copy(vertical = 0.dp))
        ) {
            DropdownMenu(
                expanded = openSelector,
                onDismissRequest = { openSelector = false },
                shape = theme.dropdownShape,
            ) {
                for (itemValue in entries) {
                    item(itemValue, value) {
                        onValueChange(itemValue)
                        openSelector = false
                    }
                }
            }
        }
    }
    Preference(
        title = title,
        modifier = modifier,
        enabled = enabled,
        icon = icon,
        summary = summary,
        showDivider = showDivider,
    ) {
        openSelector = true
    }
}

@PublishedApi
internal object ListPreferenceDefaults {
    fun <T> item(
        valueToText: (T) -> AnnotatedString,
    ): @Composable (value: T, currentValue: T, onClick: () -> Unit) -> Unit =
        { value, currentValue, onClick ->
            DropdownMenuItem(
                text = {
                    Text(text = valueToText(value))
                },
                onClick = onClick,
                colors = MenuDefaults.itemColors(
                    textColor = if (value == currentValue) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface,
                ),
                contentPadding = PaddingValues(vertical = 13.dp, horizontal = 24.dp),
                trailingIcon = {},
            )
        }
}
