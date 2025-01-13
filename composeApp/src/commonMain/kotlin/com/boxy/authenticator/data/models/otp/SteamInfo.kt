package com.boxy.authenticator.data.models.otp

import com.boxy.authenticator.helpers.otp.TOTP
import com.boxy.authenticator.utils.Base32
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SteamInfo(
    secretKey: ByteArray,
) : TotpInfo(secretKey, digits = DIGITS) {

    override fun getOtp(time: Long): String {
        val otp = TOTP.generateOTP(secretKey, algorithm, digits, period.toLong(), time)
        return otp.toSteamString()
    }

    override fun toJson(): JsonObject {
        val obj = super.toJson()
        return try {
            buildJsonObject {
                obj.forEach { put(it.key, it.value) }

                put("type", getTypeId())
                put("secret", Base32.encode(secretKey))
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun getTypeId() = ID

    companion object {
        const val ID: String = "steam"
        const val DIGITS: Int = 5
    }
}
