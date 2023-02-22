package com.ps.tokky.models

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base32
import com.ps.tokky.utils.TokenCalculator
import com.ps.tokky.utils.formatOTP

class TokenEntry(
    val issuer: String,
    val label: String,
    private val secretKey: ByteArray,
    val otpLength: OTPLength,
    val period: Int,
    val algorithm: HashAlgorithm
) {

    private var currentOTP: Int = 0
    private var lastUpdatedCounter: Long = 0L

    constructor(
        issuer: String,
        label: String,
        secretKey: String,
        otpLength: OTPLength,
        period: Int,
        algorithm: HashAlgorithm
    ) : this(issuer, label, Base32().decode(secretKey), otpLength, period, algorithm) {
    }

    val secretKeyEncoded: String
        get() {
            return String(Base32().encode(secretKey))
        }

    fun updateOTP(): Boolean {
        val time = System.currentTimeMillis() / 1000
        val count = time / period

        if (count > lastUpdatedCounter) {
            currentOTP = TokenCalculator.TOTP_RFC6238(secretKey, period, otpLength, algorithm, 0)
            lastUpdatedCounter = count
            return true
        }
        return false
    }

    val otpFormatted: String
        get() = currentOTP.formatOTP(otpLength)

    override fun toString(): String {
        return "Issuer: $issuer\n" +
                "Label: $label\n" +
                "SecretKey: $secretKeyEncoded\n" +
                "OTP: ${currentOTP}\n"
    }
}
