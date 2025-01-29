package com.boxy.authenticator.navigation

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.stack.animation.isFront
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimator
import com.arkivanov.essenty.backhandler.BackHandler

actual fun <C : Any, T : Any> backAnimation(
    backHandler: BackHandler,
    onBack: () -> Unit,
): StackAnimation<C, T> = stackAnimation(androidBackAnimation())

private fun androidBackAnimation(animationSpec: FiniteAnimationSpec<Float> = tween(250)): StackAnimator =
    stackAnimator(animationSpec = animationSpec) { factor, direction, content ->
        val adjustedAlpha = if (factor <= 0.3f) 1f
        else 1f - ((factor - 0.3f) * (1f / 0.7f))

        content(
            Modifier
                .graphicsLayer(alpha = adjustedAlpha)
                .offsetYFactor(factor = if (direction.isFront) factor else factor * 0.5f)
        )
    }

private fun Modifier.offsetYFactor(factor: Float): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val offsetY = (96.dp * factor).roundToPx()

        layout(placeable.width, placeable.height) {
            placeable.placeRelative(x = 0, y = offsetY)
        }
    }