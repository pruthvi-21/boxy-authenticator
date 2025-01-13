package com.boxy.authenticator.utils

import androidx.compose.ui.graphics.Color

object Utils {
    fun isValidTOTPAuthURL(url: String?): Boolean {
        url ?: return false
        //TODO: need to find the regex
        val regex = Regex("")
        return true
    }

    fun String.toColor(): Color {
        val hexString = this.removePrefix("#")
        return when (hexString.length) {
            6 -> Color(
                red = hexString.substring(0, 2).toInt(16) / 255f,
                green = hexString.substring(2, 4).toInt(16) / 255f,
                blue = hexString.substring(4, 6).toInt(16) / 255f,
                alpha = 1f
            )
            8 -> Color(
                alpha = hexString.substring(0, 2).toInt(16) / 255f,
                red = hexString.substring(2, 4).toInt(16) / 255f,
                green = hexString.substring(4, 6).toInt(16) / 255f,
                blue = hexString.substring(6, 8).toInt(16) / 255f
            )
            else -> throw IllegalArgumentException("Invalid hex color: $this")
        }
    }
}

fun String.formatOTP(): String {
    return this
        .reversed()
        .replace(".".repeat(3).toRegex(), "$0 ")
        .trim()
        .reversed()
}

fun String.getInitials(): String {
    if (isEmpty()) return "?"
    val words = trim().split("\\s+".toRegex())
    var initials = ""

    for (i in 0 until minOf(words.size, 2)) {
        initials += words[i][0]
    }

    return initials.uppercase()
}

fun String.cleanSecretKey(): String {
    return this.replace("\\s", "").uppercase()
}