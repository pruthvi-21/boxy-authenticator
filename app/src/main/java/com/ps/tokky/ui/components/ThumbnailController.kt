package com.ps.tokky.ui.components

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.ps.tokky.R
import com.ps.tokky.utils.Constants.THUMBNAIL_COlORS

@Composable
fun ThumbnailController(
    text: String,
    colorValue: Int,
    onColorChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(15.dp)
                .size(
                    width = dimensionResource(R.dimen.thumbnail_preview_width),
                    height = dimensionResource(R.dimen.thumbnail_preview_height)
                )
                .background(
                    androidx.compose.ui.graphics.Color(colorValue),
                    shape = MaterialTheme.shapes.extraSmall,
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineLarge,
                color = androidx.compose.ui.graphics.Color(Color.WHITE)
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            THUMBNAIL_COlORS.forEachIndexed { index, color ->
                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.thumbnail_color_tile_size))
                        .clip(
                            if (color == colorValue) CircleShape
                            else MaterialTheme.shapes.extraSmall
                        )
                        .background(androidx.compose.ui.graphics.Color(color))
                        .clickable {
                            onColorChanged(color)
                        }
                )
            }
        }
    }
}