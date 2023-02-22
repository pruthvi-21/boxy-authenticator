package com.ps.tokky.utils

import android.annotation.SuppressLint
import android.content.Context
import com.ps.tokky.models.OTPLength

object Utils{
    fun getThemeColorFromAttr(context: Context, colorAttr: Int): Int {
        val arr = context.theme.obtainStyledAttributes(intArrayOf(colorAttr))
        val colorValue = arr.getColor(0, -1)
        arr.recycle()
        return colorValue
    }
}

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