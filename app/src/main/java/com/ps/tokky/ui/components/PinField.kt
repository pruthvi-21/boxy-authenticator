package com.ps.tokky.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ps.tokky.utils.Constants.LOGIN_PIN_LENGTH

@Composable
fun PinField(
    modifier: Modifier = Modifier,
    pinLength: Int
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        for (i in 0 until LOGIN_PIN_LENGTH) {
            val isFilled = (pinLength > i)
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFilled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
            )
        }
    }
}