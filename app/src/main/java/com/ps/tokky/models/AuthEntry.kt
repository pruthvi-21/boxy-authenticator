package com.ps.tokky.models

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base32
import com.ps.tokky.utils.TokenCalculator

class AuthEntry(
    val issuer: String,
    val label: String,
    private val secretKey: ByteArray,
    val otpLength: OTPLength,
    val period: Int,
    val algorithm: HashAlgorithm
) {

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

    val getOTP: String
        get() {
            return TokenCalculator.TOTP_RFC6238(secretKey, period, otpLength, algorithm, 0)
        }

    override fun toString(): String {
        return "Issuer: $issuer\n" +
                "Label: $label\n" +
                "SecretKey: $secretKeyEncoded\n" +
                "OTP: ${getOTP}\n"
    }
}
