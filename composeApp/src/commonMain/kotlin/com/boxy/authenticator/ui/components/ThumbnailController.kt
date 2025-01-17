package com.boxy.authenticator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.boxy.authenticator.data.models.Thumbnail
import com.boxy.authenticator.utils.Constants.THUMBNAIL_COlORS
import com.boxy.authenticator.utils.Constants.THUMBNAIL_ICONS
import com.boxy.authenticator.utils.Utils.toColor

@Composable
fun ThumbnailController(
    text: String,
    thumbnail: Thumbnail,
    onThumbnailChanged: (Thumbnail) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PreviewLayout(text = text, thumbnail = thumbnail, onThumbnailChanged = onThumbnailChanged)

        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            THUMBNAIL_COlORS.forEachIndexed { index, color ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(
                            when (thumbnail) {
                                is Thumbnail.Color -> if (thumbnail.color == color) CircleShape
                                else MaterialTheme.shapes.extraSmall

                                is Thumbnail.Icon -> MaterialTheme.shapes.extraSmall
                            }
                        )
                        .background(color.toColor())
                        .clickable {
                            onThumbnailChanged(Thumbnail.Color(color))
                        }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewLayout(
    text: String,
    thumbnail: Thumbnail,
    onThumbnailChanged: (Thumbnail) -> Unit,
) {
    var showIconPickerSheet by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Dummy
        Box(Modifier.size(44.dp))

        TokenThumbnail(
            thumbnail = thumbnail,
            text = text,
            width = 100.dp,
            modifier = Modifier.padding(15.dp),
        )

        IconButton(
            onClick = { showIconPickerSheet = true },
            modifier = Modifier.size(44.dp)
        ) {
            Icon(Icons.Outlined.Edit, contentDescription = "", modifier = Modifier.padding(10.dp))
        }
    }

    if (showIconPickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showIconPickerSheet = false }
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(100.dp),
                modifier = Modifier.padding(horizontal = 10.dp),
            ) {
                items(THUMBNAIL_ICONS.sortedBy { it.label }) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            onThumbnailChanged(it)
                            showIconPickerSheet = false
                        }
                    ) {
                        TokenThumbnail(
                            thumbnail = it,
                            width = 70.dp,
                            modifier = Modifier.padding(vertical = 10.dp),
                        )
                        Text(
                            it.label,
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                            minLines = 2,
                            maxLines = 2,
                        )
                    }
                }
            }
        }
    }
}