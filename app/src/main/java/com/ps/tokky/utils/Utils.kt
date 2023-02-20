package com.ps.tokky.utils

import com.ps.tokky.models.OTPLength

object Utils {
    fun formatTokenString(token: Int, otpLength: OTPLength): String {
        return "$token".padStart(otpLength.value, '0')
    }
}