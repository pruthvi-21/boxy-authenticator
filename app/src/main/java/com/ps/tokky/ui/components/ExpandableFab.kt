package com.ps.tokky.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ps.tokky.R
import kotlin.math.roundToInt

private const val DURATION = 200

@Composable
fun ExpandableFab(
    isFabExpanded: Boolean,
    onFabClick: () -> Unit,
    onScrimClick: () -> Unit,
    onQrClick: () -> Unit,
    onManualClick: () -> Unit,
    windowPadding: PaddingValues,
    modifier: Modifier = Modifier
) {

    val fabEnterTransition = slideInVertically(
        animationSpec = tween(
            durationMillis = DURATION,
            easing = FastOutSlowInEasing
        ),
        initialOffsetY = { (it / 1.5).roundToInt() }
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = DURATION,
            easing = FastOutSlowInEasing
        ),
    )

    val fabExitTransition = slideOutVertically(
        animationSpec = tween(
            durationMillis = DURATION,
            easing = FastOutSlowInEasing
        ),
        targetOffsetY = { (it / 1.5).roundToInt() }
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = DURATION,
            easing = FastOutSlowInEasing
        ),
    )

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = isFabExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onScrimClick() }
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(end = windowPadding.calculateEndPadding(LayoutDirection.Ltr))
        ) {
            AnimatedVisibility(
                visible = isFabExpanded,
                enter = fabEnterTransition,
                exit = fabExitTransition
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    FabItem(
                        text = stringResource(R.string.expandable_fab_qr_title),
                        icon = painterResource(R.drawable.ic_add_qr),
                        onClick = onQrClick
                    )

                    FabItem(
                        text = stringResource(R.string.expandable_fab_manual_title),
                        icon = painterResource(R.drawable.ic_add_manual),
                        onClick = onManualClick
                    )

                    Spacer(Modifier.height(20.dp))
                }
            }

            val rotationValue by animateFloatAsState(
                targetValue = if (isFabExpanded) 135f else 0f,
                animationSpec = tween(durationMillis = DURATION),
                label = "FabRotationValue"
            )

            FloatingActionButton(
                onClick = onFabClick,
                modifier = Modifier
                    .padding(bottom = windowPadding.calculateBottomPadding())
                    .rotate(rotationValue)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_fab_add_new)
                )
            }
        }
    }
}

@Composable
private fun FabItem(
    text: String,
    icon: Painter,
    onClick: () -> Unit
) {
    val background = MaterialTheme.colorScheme.surfaceContainer
    val onBackground = MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_tiny)))
            .clickable(onClick = onClick)
            .background(background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = onBackground,
        )

        Spacer(modifier = Modifier.width(15.dp))

        Image(
            painter = icon,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(onBackground),
            modifier = Modifier
                .size(24.dp)
        )
    }
}
