package com.boxy.authenticator.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.refresh
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.models.enums.TokenTapResponse
import com.boxy.authenticator.domain.models.otp.HotpInfo
import com.boxy.authenticator.domain.models.otp.SteamInfo
import com.boxy.authenticator.domain.models.otp.TotpInfo
import com.boxy.authenticator.ui.components.BoxProgressBar
import com.boxy.authenticator.ui.components.TokenThumbnail
import com.boxy.authenticator.ui.viewmodels.LocalSettingsViewModel
import com.boxy.authenticator.utils.formatOTP
import com.boxy.authenticator.utils.getInitials
import com.boxy.authenticator.utils.name
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

private const val SLIDE_DURATION = 150

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TokensList(
    accounts: List<TokenEntry>,
    onEdit: (token: TokenEntry) -> Unit,
    singleExpansion: Boolean = true,
) {
    val expandedStates = remember { mutableStateMapOf<TokenEntry, Boolean>() }

    val groupedAccounts = accounts
        .sortedBy { it.name }
        .groupBy { it.name.first().uppercaseChar() }

    val cardShape = MaterialTheme.shapes.medium

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
                    tokens.size == 1 -> cardShape
                    index == 0 -> cardShape.copy(
                        bottomStart = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp),
                    )

                    index == tokens.lastIndex -> cardShape.copy(
                        topStart = CornerSize(0.dp),
                        topEnd = CornerSize(0.dp),
                    )

                    else -> RectangleShape
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
    modifier: Modifier = Modifier,
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
                thumbnail = token.thumbnail,
                text = token.issuer.getInitials(),
                width = 55.dp,
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

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                animationSpec = tween(SLIDE_DURATION)
            ),
            exit = shrinkVertically(
                animationSpec = tween(SLIDE_DURATION)
            )
        ) {
            when (token.otpInfo) {
                is HotpInfo -> HOTPFieldView(token.otpInfo)
                is TotpInfo -> TOTPFieldView(token.otpInfo)
            }
        }
    }
}

@Composable
private fun HOTPFieldView(otpInfo: HotpInfo) {
    var counter by remember { mutableLongStateOf(otpInfo.counter) }
    var otp by remember { mutableStateOf(otpInfo.getOtp()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 55.dp)
            .padding(top = 10.dp)
    ) {
        Text(
            text = "#$counter",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(37.5.dp)
        )
        OTPValueDisplay(value = otp)

        Spacer(Modifier.weight(1f))

        IconButton(onClick = {
            otpInfo.incrementCounter()
            counter = otpInfo.counter
            otp = otpInfo.getOtp()
        }) {
            Icon(Icons.Rounded.Refresh, contentDescription = stringResource(Res.string.refresh))
        }
    }
}

@Composable
private fun TOTPFieldView(
    otpInfo: TotpInfo,
) {
    data class OtpState(
        val value: String,
        val progress: Float,
        val duration: Long,
        val isAnimating: Boolean = true,
    )

    val coroutineScope = rememberCoroutineScope()
    val totalPeriodMillis = otpInfo.period * 1000L

    val otpState = remember {
        mutableStateOf(
            OtpState(
                value = otpInfo.getOtp(),
                progress = otpInfo.getMillisTillNextRotation().toFloat() / totalPeriodMillis,
                duration = otpInfo.getMillisTillNextRotation() % totalPeriodMillis
            )
        )
    }

    LaunchedEffect(otpInfo) {
        while (true) {
            delay(otpState.value.duration)
            otpState.value = OtpState(
                value = otpInfo.getOtp(),
                progress = 1f,
                duration = totalPeriodMillis
            )
        }
    }

    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            coroutineScope.launch {
                otpState.value = OtpState(
                    value = otpInfo.getOtp(),
                    progress = otpInfo.getMillisTillNextRotation().toFloat() / totalPeriodMillis,
                    duration = otpInfo.getMillisTillNextRotation() % totalPeriodMillis
                )
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "OTP Progress")
    val progressAnimationValue by infiniteTransition.animateFloat(
        initialValue = otpState.value.progress,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = otpState.value.duration.toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "OTP Progress Animation"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = 55.dp,
                    height = 37.5.dp,
                ), contentAlignment = Alignment.Center
        ) {
            BoxProgressBar(
                progress = progressAnimationValue,
                width = 28.dp,
                height = 28.dp,
            )
        }

        OTPValueDisplay(
            value = otpState.value.value,
            formatOTP = otpInfo !is SteamInfo
        )
    }
}

@Composable
private fun OTPValueDisplay(
    value: String,
    formatOTP: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val settingsViewModel = LocalSettingsViewModel.current
    val tokenTapResponse = settingsViewModel.tokenTapResponse.value

    val clipboardManager = LocalClipboardManager.current
    val hapticFeedback = LocalHapticFeedback.current

    fun copyToClipboard() {
        clipboardManager.setText(AnnotatedString(value))
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Text(
        text = if (formatOTP) value.formatOTP() else value,
        style = MaterialTheme.typography.titleLarge.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 34.sp
        ),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .padding(horizontal = 15.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (tokenTapResponse == TokenTapResponse.SINGLE_TAP) {
                            copyToClipboard()
                        }
                    },
                    onDoubleTap = {
                        if (tokenTapResponse == TokenTapResponse.DOUBLE_TAP) {
                            copyToClipboard()
                        }
                    },
                    onLongPress = {
                        if (tokenTapResponse == TokenTapResponse.LONG_PRESS) {
                            copyToClipboard()
                        }
                    }
                )
            },
    )
}

@Composable
private fun Arrow(
    onEdit: () -> Unit,
    isExpanded: Boolean,
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
private fun LabelsView(
    issuer: String,
    label: String,
    modifier: Modifier,
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