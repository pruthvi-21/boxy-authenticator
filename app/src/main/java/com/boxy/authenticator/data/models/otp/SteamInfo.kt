package com.boxy.authenticator.data.models.otp

import com.boxy.authenticator.helpers.otp.TOTP
import com.boxy.authenticator.utils.Base32.encode
import com.boxy.authenticator.utils.OtpInfoException
import org.json.JSONException
import org.json.JSONObject
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException

class SteamInfo(
    secretKey: ByteArray,
) : TotpInfo(secretKey, digits = DIGITS) {

    @Throws(OtpInfoException::class)
    override fun getOtp(time: Long): String {
        try {
            val otp = TOTP.generateOTP(secretKey, algorithm, digits, period.toLong(), time)
            return otp.toSteamString()
        } catch (e: InvalidKeyException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    override fun toJson(): JSONObject {
        val obj = JSONObject()

        try {
            obj.put("type", getTypeId())
            obj.put("secret", encode(secretKey))
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }

        return obj
    }

    override fun getTypeId() = ID

    companion object {
        const val ID: String = "steam"
        const val DIGITS: Int = 5
    }
}