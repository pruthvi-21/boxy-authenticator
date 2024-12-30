package com.ps.tokky.helpers.otp

import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import kotlin.math.floor

object TOTP {

    @Throws(InvalidKeyException::class, NoSuchAlgorithmException::class)
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

    @Throws(InvalidKeyException::class, NoSuchAlgorithmException::class)
    fun generateOTP(secret: ByteArray, algo: String, digits: Int, period: Long): OTP {
        return generateOTP(secret, algo, digits, period, System.currentTimeMillis() / 1000)
    }
}