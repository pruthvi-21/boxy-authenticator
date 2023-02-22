package com.ps.tokky.utils

import android.annotation.SuppressLint
import com.ps.tokky.models.OTPLength

fun Int.formatOTP(length: OTPLength): String {
    return "$this"
        .padStart(length.value, '0')
        .replace(".".repeat(length.chunkSize).toRegex(), "$0 ")
        .trim()
}

@SuppressLint("DefaultLocale")
fun String.cleanSecretKey(): String {
    return this.replace("\\s", "").toUpperCase()
}

fun String.isValidSecretKey(): Boolean {
    return Regex(Constants.BASE32_CHARS).matches(this)
}