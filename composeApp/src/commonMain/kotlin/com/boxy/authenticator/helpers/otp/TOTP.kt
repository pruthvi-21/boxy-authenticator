package com.boxy.authenticator.helpers.otp

import kotlinx.datetime.Clock
import kotlin.math.floor

object TOTP {

    fun generateOTP(
        secret: ByteArray,
        algo: String,
        digits: Int,
        period: Long,
        seconds: Long,
    ): OTP {
        val counter = floor(seconds.toDouble() / period).toLong()
        return HOTP.generateOTP(secret, algo, digits, counter)
    }

    fun generateOTP(secret: ByteArray, algo: String, digits: Int, period: Long): OTP {
        return generateOTP(
            secret = secret,
            algo = algo,
            digits = digits,
            period = period,
            seconds = Clock.System.now().toEpochMilliseconds() / 1000
        )
    }
}