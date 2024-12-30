package com.ps.tokky.helpers.otp

class OTP(
    val code: Int,
    val digits: Int,
) {
    override fun toString(): String {
        return "$code".takeLast(digits).padStart(digits, '0')
    }
}