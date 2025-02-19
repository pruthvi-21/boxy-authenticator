package com.boxy.authenticator.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

data class ExpandableFabItem(
    val label: String,
    val icon: ImageVector,
)

@Composable
expect fun ExpandableFab(
    isFabExpanded: Boolean,
    items: List<ExpandableFabItem>,
    onItemClick: (index: Int) -> Unit,
    onFabExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
)
