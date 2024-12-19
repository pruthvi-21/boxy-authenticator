package com.ps.tokky.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ps.tokky.R

@Composable
fun DefaultAppBarNavigationIcon(onNavigationIconClick: () -> Unit) {
    IconButton(onClick = onNavigationIconClick) {
        Image(
            painterResource(R.drawable.ic_samsung_arrow_left),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
        )
    }
}
