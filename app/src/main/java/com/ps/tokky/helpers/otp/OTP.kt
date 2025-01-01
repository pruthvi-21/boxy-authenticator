package com.ps.tokky.helpers.otp

class OTP(
    val code: Int,
    val digits: Int,
) {
    override fun toString(): String {
        return "$code".takeLast(digits).padStart(digits, '0')
    }

    fun toSteamString(): String {
        var tempCode = code
        return buildString {
            repeat(digits) {
                append(STEAM_ALPHABET[tempCode % STEAM_ALPHABET.length])
                tempCode /= STEAM_ALPHABET.length
            }
        }
    }

    companion object {
        private const val STEAM_ALPHABET: String = "23456789BCDFGHJKMNPQRTVWXY"
    }
}