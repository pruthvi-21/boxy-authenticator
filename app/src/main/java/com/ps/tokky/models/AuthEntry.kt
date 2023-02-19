package com.ps.tokky.models

data class AuthEntry(
    val issuer: String,
    val label: String,
    val secretKey: String,
    val otpLength: OTPLength,
    val period: Int,
    val algo: HashAlgorithm
) {
    override fun toString(): String {
        return "Issuer: $issuer\n" +
                "Label: $label\n" +
                "SecretKey: $secretKey\n" +
                "OTP Length: ${otpLength.title}\n" +
                "Period: $period\n" +
                "Algo: ${algo.name}\n"
    }
}
