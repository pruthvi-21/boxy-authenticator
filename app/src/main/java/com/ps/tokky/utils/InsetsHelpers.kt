package com.ps.tokky.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun PaddingValues.toWindowInsets(): WindowInsets {
    return WindowInsets(
        left = this.calculateLeftPadding(LocalLayoutDirection.current),
        top = this.calculateTopPadding(),
        right = this.calculateRightPadding(LocalLayoutDirection.current),
        bottom = this.calculateBottomPadding()
    )
}

fun PaddingValues.start(layoutDirection: LayoutDirection): Dp {
    return this.calculateStartPadding(layoutDirection)
}

fun PaddingValues.top(): Dp {
    return this.calculateTopPadding()
}

fun PaddingValues.end(layoutDirection: LayoutDirection): Dp {
    return this.calculateEndPadding(layoutDirection)
}

fun PaddingValues.bottom(): Dp {
    return this.calculateBottomPadding()
}

@Composable
fun PaddingValues.copy(
    start: Dp = calculateLeftPadding(LocalLayoutDirection.current),
    top: Dp = calculateTopPadding(),
    end: Dp = calculateRightPadding(LocalLayoutDirection.current),
    bottom: Dp = calculateBottomPadding()
): PaddingValues {
    return PaddingValues(start = start, top = top, end = end, bottom = bottom)
}

@Composable
fun WindowInsets.copy(
    start: Dp? = null,
    top: Dp? = null,
    end: Dp? = null,
    bottom: Dp? = null
): WindowInsets {
    val original = this.asPaddingValues()
    return original.copy(
        start = (start ?: original.calculateLeftPadding(LocalLayoutDirection.current)),
        top = (top ?: original.calculateTopPadding()),
        end = (end ?: original.calculateRightPadding(LocalLayoutDirection.current)),
        bottom = (bottom ?: original.calculateBottomPadding())
    ).toWindowInsets()
}