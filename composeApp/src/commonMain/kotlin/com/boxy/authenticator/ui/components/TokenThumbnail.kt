package com.boxy.authenticator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import boxy_authenticator.composeapp.generated.resources.Res
import coil3.compose.AsyncImage
import com.boxy.authenticator.data.models.Thumbnail
import com.boxy.authenticator.utils.Constants.THUMBNAIL_ICON_PATH
import com.boxy.authenticator.utils.Utils.toColor
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TokenThumbnail(
    thumbnail: Thumbnail,
    text: String = "",
    width: Dp,
    modifier: Modifier = Modifier,
) {
    val fontSize = (width.value * 0.35).sp
    Box(
        modifier = modifier
            .width(width)
            .aspectRatio(220f / 150)
            .clip(MaterialTheme.shapes.extraSmall)
            .then(
                when (thumbnail) {
                    is Thumbnail.Color -> Modifier.background(thumbnail.color.toColor())
                    is Thumbnail.Icon -> Modifier
                }
            )
            .border(
                width = .5.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.extraSmall,
            )
    ) {
        when (thumbnail) {
            is Thumbnail.Color -> Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    letterSpacing = 0.07.em,
                    fontSize = fontSize,
                ),
                modifier = Modifier.align(Alignment.Center)
            )

            is Thumbnail.Icon -> AsyncImage(
                Res.getUri("$THUMBNAIL_ICON_PATH/${thumbnail.path}"),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}