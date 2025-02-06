package com.boxy.authenticator.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.boxy.authenticator.domain.models.enums.TokenTapResponse
import com.boxy.authenticator.ui.viewmodels.LocalSettingsViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OtpTextView(otp: String, modifier: Modifier = Modifier) {
    val settingsViewModel = LocalSettingsViewModel.current
    val tokenTapResponse = settingsViewModel.tokenTapResponse.value

    val clipboardManager = LocalClipboardManager.current
    val hapticFeedback = LocalHapticFeedback.current

    fun copyToClipboard() {
        clipboardManager.setText(AnnotatedString(otp))
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val chunks = remember(otp) { otp.customChunk() }

    FlowRow(
        modifier = modifier
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
    ) {
        chunks.forEach { chunk ->
            Text(
                text = chunk,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 34.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(
                    start = if (chunks.first() == chunk) 0.dp else 5.dp,
                    end = if (chunks.last() == chunk) 0.dp else 5.dp,
                )
            )
        }
    }
}

private fun String.customChunk(): List<String> {
    return when (length) {
        6, 8 -> chunked(length / 2)
        7 -> listOf(take(4), drop(4))
        9 -> listOf(take(3), substring(3, 6), drop(6))
        10 -> listOf(take(4), substring(4, 8), drop(8))
        else -> listOf(this)
    }
}