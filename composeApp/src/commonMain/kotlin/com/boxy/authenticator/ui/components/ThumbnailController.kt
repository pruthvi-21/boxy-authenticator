package com.boxy.authenticator.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.boxy.authenticator.utils.Constants.THUMBNAIL_COlORS
import com.boxy.authenticator.utils.Utils
import com.boxy.authenticator.utils.Utils.toColor

@Composable
fun ThumbnailController(
    text: String,
    colorHex: String,
    onColorChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(15.dp)
                .size(
                    width = 100.dp,
                    height = 68.1818.dp,
                )
                .clip(MaterialTheme.shapes.extraSmall)
                .background(colorHex.toColor()),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
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
                        .size(36.dp)
                        .clip(
                            if (colorHex == color) CircleShape
                            else MaterialTheme.shapes.extraSmall
                        )
                        .background(color.toColor())
                        .clickable {
                            onColorChanged(colorHex)
                        }
                )
            }
        }
    }
}