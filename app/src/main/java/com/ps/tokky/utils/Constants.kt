package com.ps.tokky.utils

import android.annotation.SuppressLint
import com.ps.tokky.models.HashAlgorithm
import com.ps.tokky.models.OTPLength

object Constants {
    val DEFAULT_OTP_LENGTH = OTPLength.LEN_6
    val DEFAULT_HASH_ALGORITHM = HashAlgorithm.SHA1

    const val BASE32_CHARS = "[A-Z2-7 ]+"
}

@SuppressLint("DefaultLocale")
fun String.formatSecretKey(): String {
    return this.replace("\\s", "").toUpperCase()
}

fun String.isValidSecretKey(): Boolean {
    return Regex(Constants.BASE32_CHARS).matches(this)
}