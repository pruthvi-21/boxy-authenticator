package com.ps.tokky.utils

import com.ps.tokky.models.OTPLength

object Constants {
    const val DEFAULT_OTP_VALIDITY = 30
    val DEFAULT_OTP_LENGTH = OTPLength.LEN_6
    const val DEFAULT_HASH_ALGORITHM = "SHA1"

    const val OTP_GENERATION_REFRESH_INTERVAL = 1000L

    const val BASE32_CHARS = "[A-Z2-7 ]+"
}