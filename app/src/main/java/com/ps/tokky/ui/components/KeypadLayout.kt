package com.ps.tokky.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val gridCount = 3

@Composable
fun KeypadLayout(
    modifier: Modifier = Modifier,
    onKeyClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onSubmit: () -> Unit,
) {
    val numbers = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "⌫", "0", "→"
    )

    fun isBackspace(key: String): Boolean = key == "⌫"
    fun isSubmit(key: String): Boolean = key == "→"
    fun isSpecial(key: String): Boolean = isBackspace(key) || isSubmit(key)

    BoxWithConstraints {

        val gridModifier = modifier
            .aspectRatio(3 / 4f)

        if (maxWidth >= maxHeight) gridModifier.fillMaxHeight()
        else gridModifier.fillMaxWidth()

        LazyVerticalGrid(
            columns = GridCells.Fixed(gridCount),
            modifier = gridModifier
        ) {
            items(numbers) { key ->
                var backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                var textColor = MaterialTheme.colorScheme.onSurface
                var fontSize = 30.sp

                if (isSpecial(key)) {
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer
                    textColor = MaterialTheme.colorScheme.onSecondaryContainer
                    fontSize = 24.sp
                }

                AnimatedKey(
                    key = key,
                    backgroundColor = backgroundColor,
                    textColor = textColor,
                    fontSize = fontSize,
                    onKeyClick = {
                        when {
                            isBackspace(key) -> onBackspaceClick()
                            isSubmit(key) -> onSubmit()
                            else -> onKeyClick(key)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun calculateKeySize(): Dp {
    val availableWidth = LocalContext.current.resources.displayMetrics.widthPixels.dp
    val itemSize = (availableWidth / gridCount).coerceAtMost(105.dp)
    return itemSize
}

@Composable
private fun AnimatedKey(
    key: String,
    backgroundColor: Color,
    textColor: Color,
    fontSize: TextUnit,
    onKeyClick: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    Box(
        modifier = Modifier
            .padding(6.dp)
            .aspectRatio(1f)
            .width(calculateKeySize())
            .height(calculateKeySize())
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable {
                onKeyClick()
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
    ) {
        Text(
            text = key,
            fontSize = fontSize,
            color = textColor,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}