package com.ps.tokky.ui.components.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun PreferenceCategoryScope.preference(
    title: String,
    summary: String? = null,
    onClick: () -> Unit = {},
    disabled: Boolean = false,
    modifier: Modifier = Modifier
) {
    this.addPreference {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .clickable(enabled = !disabled) {
                    onClick()
                }
                .padding(horizontal = 32.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp)
            ) {
                PreferenceTitle(title, disabled)
                if (summary != null) {
                    PreferenceSummary(summary, disabled)
                }
            }
        }
    }
}