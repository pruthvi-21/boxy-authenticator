package com.boxy.authenticator.helpers

import android.content.Context
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.boxy.authenticator.utils.toPx

class TransitionHelper(
    val context: Context
) {
    val screenEnterAnim: EnterTransition
        get() {
            val slideUp = slideIn(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) { IntOffset(0, 96.dp.toPx(context)) }
            val fadeIn = fadeIn(
                animationSpec = tween(
                    durationMillis = 83,
                    delayMillis = 50,
                    easing = LinearEasing
                )
            )

            return slideUp + fadeIn
        }

    val screenExitAnim: ExitTransition
        get() {
            val slideDown = slideOut(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) { IntOffset(0, -54.dp.toPx(context)) }
            val fadeOut = fadeOut(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )

            return slideDown + fadeOut
        }


    val screenPopEnterAnim: EnterTransition
        get() {
            val slideDown = slideIn(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) { IntOffset(0, -54.dp.toPx(context)) }

            return slideDown
        }

    val screenPopExitAnim: ExitTransition
        get() {
            val slideDown = slideOut(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) { IntOffset(0, 96.dp.toPx(context)) }

            val fadeOut = fadeOut(
                animationSpec = tween(
                    durationMillis = 83,
                    delayMillis = 35,
                    easing = LinearEasing
                )
            )

            return slideDown + fadeOut
        }

}