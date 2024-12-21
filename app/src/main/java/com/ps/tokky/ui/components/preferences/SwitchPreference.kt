package com.ps.tokky.ui.components.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun PreferenceCategoryScope.switchPreference(
    title: String,
    summary: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit = {},
    disabled: Boolean = false,
    modifier: Modifier = Modifier
) {
    this.addPreference {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .clickable(enabled = !disabled) {
                    onCheckedChange(!checked)
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
                    Spacer(Modifier.height(5.dp))
                    PreferenceSummary(summary, disabled)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Switch(
                checked = checked,
                onCheckedChange = null,
                enabled = !disabled
            )
        }
    }
}