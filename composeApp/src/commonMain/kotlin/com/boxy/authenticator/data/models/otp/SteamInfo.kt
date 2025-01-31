package com.boxy.authenticator.data.models.otp

import com.boxy.authenticator.helpers.serializers.SteamInfoSerializer
import com.boxy.authenticator.helpers.otp.TOTP
import kotlinx.serialization.Serializable

@Serializable(with = SteamInfoSerializer::class)
class SteamInfo(
    secretKey: ByteArray,
) : TotpInfo(secretKey, digits = DIGITS) {

    override fun getOtp(time: Long): String {
        val otp = TOTP.generateOTP(secretKey, algorithm, digits, period.toLong(), time)
        return otp.toSteamString()
    }

    companion object {
        const val DIGITS: Int = 5
    }
}
