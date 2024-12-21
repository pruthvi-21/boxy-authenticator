package com.ps.tokky.ui.components.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PreferenceCategoryScope internal constructor() {
    internal val preferences = mutableListOf<@Composable () -> Unit>()

    internal fun addPreference(preference: @Composable () -> Unit) {
        preferences.add(preference)
    }
}

@Composable
fun PreferenceCategory(
    title: String? = null,
    showDividers: Boolean = true,
    content: PreferenceCategoryScope.() -> Unit
) {
    val cornerRadius = 16.dp

    val scope = PreferenceCategoryScope()
    scope.content()

    Column {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 32.dp)
                    .padding(top = 8.dp)
            )
        } else {
            Spacer(Modifier.height(15.dp))
        }

        LazyColumn {
            itemsIndexed(scope.preferences) { index, item ->
                val isFirstItem = index == 0
                val isLastItem = index == scope.preferences.size - 1
                val shape = RoundedCornerShape(
                    topStart = if (isFirstItem) cornerRadius else 0.dp,
                    topEnd = if (isFirstItem) cornerRadius else 0.dp,
                    bottomStart = if (isLastItem) cornerRadius else 0.dp,
                    bottomEnd = if (isLastItem) cornerRadius else 0.dp
                )

                Box(
                    modifier = Modifier
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    item()
                }

                if (showDividers && !isLastItem) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 1.dp,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 32.dp)
                    )
                }
            }
        }
    }
}