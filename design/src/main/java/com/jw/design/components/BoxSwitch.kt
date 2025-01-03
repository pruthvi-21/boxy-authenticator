package com.jw.design.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

@Composable
fun BoxSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trackWidth: Dp = 46.dp,
    trackHeight: Dp = 28.dp,
    trackStroke: Dp = 1.5.dp,
    trackShape: Shape = RoundedCornerShape(2.dp),
    thumbShape: Shape = RoundedCornerShape(2.dp),
    colors: SwitchColors = SwitchDefaults.colors(),
) {
    val thumbOffset by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(150),
        label = "BoxSwitchAnimation"
    )

    var trackColor = colors.checkedTrackColor
    var thumbColor = colors.checkedThumbColor
    var strokeColor = colors.checkedTrackColor

    if (!checked) {
        trackColor = colors.uncheckedTrackColor
        thumbColor = colors.uncheckedThumbColor
        strokeColor = colors.uncheckedBorderColor
    }

    if (!enabled) {
        if (checked) {
            trackColor = colors.disabledCheckedTrackColor
            thumbColor = colors.disabledCheckedThumbColor
            strokeColor = colors.disabledCheckedTrackColor
        } else {
            trackColor = colors.disabledUncheckedTrackColor
            thumbColor = colors.disabledUncheckedThumbColor
            strokeColor = colors.disabledUncheckedBorderColor
        }
    }

    Box(
        modifier = modifier
            .size(trackWidth, trackHeight)
            .background(strokeColor, trackShape)
            .padding(trackStroke)
            .background(color = trackColor, shape = trackShape)
            .then(
                if (onCheckedChange != null)
                    Modifier.clickable(enabled = enabled) { onCheckedChange.invoke(!checked) }
                else Modifier
            )
    ) {
        Box(
            modifier = Modifier
                .padding((1f - thumbOffset) * 2.5.dp)
                .aspectRatio(1f)
                .offset {
                    IntOffset(
                        ((trackWidth - trackHeight) * thumbOffset).roundToPx(),
                        0
                    )
                }
                .background(color = thumbColor, shape = thumbShape)
        )
    }
}
