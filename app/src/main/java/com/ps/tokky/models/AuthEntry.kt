package com.ps.tokky.models

data class AuthEntry(
    val issuer: String,
    val label: String,
    val secretKey: String,
    val digits: Int,
    val period: Int,
    val algo: HashAlgorithm
) {
    override fun toString(): String {
        return "Issuer: $issuer\n" +
                "Label: $label\n" +
                "SecretKey: $secretKey\n" +
                "Digits: $digits\n" +
                "Period: $period\n" +
                "Algo: ${algo.name}\n"
    }
}
