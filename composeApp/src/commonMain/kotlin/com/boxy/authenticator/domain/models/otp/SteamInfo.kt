package com.boxy.authenticator.domain.models.otp

import com.boxy.authenticator.core.serialization.SteamInfoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = SteamInfoSerializer::class)
class SteamInfo(
    secretKey: ByteArray,
) : TotpInfo(secretKey, digits = DIGITS) {

    override fun getOtp(): String {
        return super.getOtp().toSteamString()
    }

    private fun String.toSteamString(): String {
        var tempCode = this.toInt()
        return buildString {
            repeat(digits) {
                append(STEAM_ALPHABET[tempCode % STEAM_ALPHABET.length])
                tempCode /= STEAM_ALPHABET.length
            }
        }
    }

    companion object {
        const val DIGITS: Int = 5
        private const val STEAM_ALPHABET: String = "23456789BCDFGHJKMNPQRTVWXY"
    }
}
