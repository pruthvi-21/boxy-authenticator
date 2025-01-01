package com.ps.tokky.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RectangularProgressBar(
    progress: Float,
    width: Dp = 24.dp,
    height: Dp = 24.dp,
    modifier: Modifier = Modifier,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    cornerRadius: Dp = 2.dp,
) {
    val sweepAngle = remember { mutableFloatStateOf(360f * progress.coerceIn(0f, 1f)) }

    LaunchedEffect(progress) {
        sweepAngle.floatValue = 360f * progress.coerceIn(0f, 1f)
    }

    Canvas(
        modifier = Modifier
            .size(width, height)
            .then(modifier)
    ) {
        // Create the rectangular path with rounded corners
        val roundRect = RoundRect(
            rect = Rect(
                offset = Offset.Zero,
                size = size
            ),
            cornerRadius = CornerRadius(cornerRadius.toPx())
        )

        val rectPath = Path().apply {
            addRoundRect(roundRect)
        }

        // Draw background
        drawPath(
            path = rectPath,
            color = backgroundColor
        )

        // Clip to rectangle shape and draw progress
        clipPath(rectPath) {
            drawArc(
                color = progressColor,
                startAngle = -90f, // Start from top
                sweepAngle = sweepAngle.floatValue,
                useCenter = true,
                size = Size(
                    width = size.width * 2f, // Make arc large enough to fill rectangle
                    height = size.height * 2f
                ),
                topLeft = Offset(
                    -size.width / 2f, // Center the arc
                    -size.height / 2f
                )
            )
        }
    }
}