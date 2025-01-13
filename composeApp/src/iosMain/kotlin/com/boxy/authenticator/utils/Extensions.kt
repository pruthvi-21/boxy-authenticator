package com.boxy.authenticator.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import platform.Foundation.setValue
import platform.UIKit.UIAlertAction
import platform.UIKit.UIColor

fun Color.toUIColor(): UIColor {
    return UIColor.colorWithRed(
        red = red.toDouble(),
        green = green.toDouble(),
        blue = blue.toDouble(),
        alpha = alpha.toDouble()
    )
}

@Composable
fun UIAlertAction.applyPrimaryColor(): UIAlertAction {
    setValue(MaterialTheme.colorScheme.primary.toUIColor(), forKey = "titleTextColor")
    return this
}