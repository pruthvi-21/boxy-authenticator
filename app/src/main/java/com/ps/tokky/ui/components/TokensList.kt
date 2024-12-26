package com.ps.tokky.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.ps.tokky.R
import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.utils.Utils
import com.ps.tokky.utils.formatOTP
import com.ps.tokky.utils.getInitials
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

private const val SLIDE_DURATION = 150
private val INDICATOR_SIZE = 25.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TokensList(
    accounts: List<TokenEntry>,
    onEdit: (token: TokenEntry) -> Unit,
    singleExpansion: Boolean = true
) {
    val expandedStates = remember { mutableStateMapOf<TokenEntry, Boolean>() }

    val groupedAccounts = accounts
        .sortedBy { it.name }
        .groupBy { it.name.first().uppercaseChar() }
    val radius = dimensionResource(R.dimen.radius_medium)

    LazyColumn {
        groupedAccounts.forEach { (letter, tokens) ->
            stickyHeader {
                Text(
                    text = letter.toString(),
                    modifier = Modifier.padding(vertical = 3.dp, horizontal = 37.5.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            itemsIndexed(tokens) { index, token ->
                val shape = when {
                    tokens.size == 1 -> RoundedCornerShape(radius)
                    index == 0 -> RoundedCornerShape(topStart = radius, topEnd = radius)
                    index == tokens.lastIndex -> RoundedCornerShape(
                        bottomStart = radius,
                        bottomEnd = radius
                    )

                    else -> RoundedCornerShape(0.dp)
                }
                TokenCard(
                    token = token,
                    onEdit = onEdit,
                    isExpanded = expandedStates[token] ?: false,
                    onToggleExpand = { isExpanded ->
                        if (singleExpansion) {
                            expandedStates.keys.forEach { expandedStates[it] = false }
                        }
                        expandedStates[token] = isExpanded
                    },
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .clip(shape)
                )
                if (index != tokens.lastIndex) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(start = 90.dp, end = 24.dp),
                    )
                }
            }
        }

        item {
            Text(
                text = "Showing ${accounts.size} entries",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.75f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)
            )
        }
    }
}

@Composable
fun TokenCard(
    token: TokenEntry,
    onEdit: (TokenEntry) -> Unit,
    isExpanded: Boolean,
    onToggleExpand: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable {
                onToggleExpand(!isExpanded)
            }
            .padding(horizontal = 24.dp, vertical = 15.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TokenThumbnail(
                thumbnailIcon = token.thumbnailIcon,
                thumbnailColor = Color(token.thumbnailColor),
                text = token.issuer.getInitials(),
            )

            Spacer(modifier = Modifier.width(4.dp))

            LabelsView(
                issuer = token.issuer,
                label = token.label,
                modifier = Modifier.weight(1f)
            )
            Arrow(
                isExpanded = isExpanded,
                onEdit = { onEdit(token) }
            )
        }

        val (otpValue, setOtpValue) = remember { mutableIntStateOf(token.otp) }
        val (remainingTime, setRemainingTime) = remember { mutableLongStateOf(token.timeRemaining) }
        val (progressValue, setProgressValue) = remember { mutableFloatStateOf(token.timeRemaining.toFloat() / token.period) }

        LaunchedEffect(Unit) {
            while (true) {
                setOtpValue(token.otp)
                setRemainingTime(token.timeRemaining)
                setProgressValue(token.timeRemaining.toFloat() / token.period)
                delay(1.seconds)
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                animationSpec = tween(SLIDE_DURATION)
            ),
            exit = shrinkVertically(
                animationSpec = tween(SLIDE_DURATION)
            )
        ) {
            OTPFieldView(
                token,
                otpValue,
                remainingTime,
                progressValue
            )
        }
    }
}

@Composable
private fun OTPFieldView(
    token: TokenEntry,
    otpValue: Int,
    remainingTime: Long,
    progressValue: Float
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = dimensionResource(id = R.dimen.card_thumbnail_width),
                    height = dimensionResource(id = R.dimen.card_thumbnail_height)
                )
        ) {
//            Text(
//                text = "$remainingTime",
//                modifier = Modifier.align(Alignment.Center)
//            )
            CircularProgressIndicator(
                progress = { 1f - progressValue },
                strokeWidth = INDICATOR_SIZE / 2,
                strokeCap = StrokeCap.Butt,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                trackColor = if (progressValue <= 0.3f) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary,
                gapSize = 0.dp,
                modifier = Modifier
                    .size(INDICATOR_SIZE)
                    .align(Alignment.Center)
            )
        }

        Text(
            text = otpValue.formatOTP(token.digits),
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 34.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 15.dp)
        )
    }
}

@Composable
private fun Arrow(
    onEdit: () -> Unit,
    isExpanded: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(40.dp)
            .alpha(.5f)
    ) {
        val animationProgress by animateFloatAsState(
            targetValue = if (isExpanded) 1f else 0f,
            animationSpec = tween(SLIDE_DURATION),
            label = "ExpandCollapseAnimation"
        )

        if (isExpanded) {
            IconButton(
                onClick = onEdit,
                modifier = Modifier
                    .fillMaxHeight()
                    .size(24.dp)
                    .aspectRatio(1f / 1)
                    .alpha(animationProgress)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(Modifier.width(20.dp))
        }

        Icon(
            imageVector = Icons.Rounded.ArrowBackIosNew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .size(20.dp)
                .fillMaxHeight()
                .graphicsLayer(rotationZ = -90f + animationProgress * 180f)
        )
    }
}

@Composable
private fun TokenThumbnail(
    thumbnailIcon: String? = null,
    thumbnailColor: Color,
    text: String = ""
) {
    val context = LocalContext.current
    val fileName = thumbnailIcon ?: ""

    val logoBitmap = remember(fileName) {
        Utils.getThumbnailFromAssets(context.assets, fileName)
    }

    Box(
        modifier = Modifier
            .size(
                width = dimensionResource(id = R.dimen.card_thumbnail_width),
                height = dimensionResource(id = R.dimen.card_thumbnail_height)
            )
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_tiny)))
            .background(thumbnailColor)
    ) {
        if (logoBitmap != null) {
            Image(
                bitmap = logoBitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    letterSpacing = 0.07.em,
                    fontSize = 17.sp
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun LabelsView(
    issuer: String,
    label: String,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text = issuer,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = Color(0xFFA6A6A6),
                    fontSize = 13.5.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}