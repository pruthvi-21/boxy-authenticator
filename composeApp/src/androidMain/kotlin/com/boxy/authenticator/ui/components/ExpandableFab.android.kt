package com.boxy.authenticator.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.cd_fab_add_new
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

private const val Duration = 200
private val Easing = FastOutSlowInEasing

@Composable
actual fun ExpandableFab(
    items: List<ExpandableFabItem>,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier,
) {
    var isFabExpanded by remember { mutableStateOf(false) }

    val expandProgress by animateFloatAsState(
        targetValue = if (isFabExpanded) 1f else 0f,
        animationSpec = tween(durationMillis = Duration, easing = Easing),
        label = "FabExpandProgress"
    )

    val fabRotationValue = expandProgress * 135f
    val scrimAlphaValue = expandProgress * 0.5f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = scrimAlphaValue))
            .then(
                if (!isFabExpanded) Modifier
                else Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = isFabExpanded
                ) { isFabExpanded = false }
            )
    ) {

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom,
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AnimatedVisibility(
                visible = isFabExpanded,
                enter = getFabItemsEnterAnimation(),
                exit = getFabItemsExitAnimation(),
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    items.mapIndexed { idx, item ->
                        FabItem(
                            text = item.label,
                            icon = item.icon,
                            onClick = {
                                isFabExpanded = false
                                onItemClick(idx)
                            }
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                }
            }

            FloatingActionButton(
                onClick = { isFabExpanded = !isFabExpanded },
                modifier = Modifier.rotate(fabRotationValue)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.cd_fab_add_new)
                )
            }
        }
    }
}

@Composable
private fun FabItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    val background = MaterialTheme.colorScheme.surfaceContainer
    val onBackground = MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable(onClick = onClick)
            .background(background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            color = onBackground,
        )

        Spacer(modifier = Modifier.width(15.dp))

        Image(
            imageVector = icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(onBackground),
        )
    }
}

fun getFabItemsEnterAnimation(): EnterTransition {
    return slideInVertically(
        animationSpec = tween(
            durationMillis = Duration,
            easing = Easing
        ),
        initialOffsetY = { (it / 1.5).roundToInt() }
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = Duration,
            easing = Easing
        )
    )
}

fun getFabItemsExitAnimation(): ExitTransition {
    return slideOutVertically(
        animationSpec = tween(
            durationMillis = Duration,
            easing = Easing
        ),
        targetOffsetY = { (it / 1.5).roundToInt() }
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = Duration,
            easing = Easing
        )
    )
}