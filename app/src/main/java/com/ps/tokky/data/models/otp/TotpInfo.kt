package com.ps.tokky.data.models.otp

import com.ps.tokky.helpers.otp.TOTP
import com.ps.tokky.utils.OtpInfoException
import org.json.JSONException
import org.json.JSONObject
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException

class TotpInfo @JvmOverloads constructor(
    secretKey: ByteArray,
    algorithm: String = DEFAULT_ALGORITHM,
    digits: Int = DEFAULT_DIGITS,
    var period: Int = DEFAULT_PERIOD,
) : OtpInfo(secretKey, algorithm, digits) {


    override fun getOtp(): String {
        return getOtp(System.currentTimeMillis() / 1000)
    }

    @Throws(OtpInfoException::class)
    fun getOtp(time: Long): String {
        try {
            val otp = TOTP.generateOTP(secretKey, algorithm, digits, period.toLong(), time)
            return otp.toString()
        } catch (e: InvalidKeyException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    fun getMillisTillNextRotation(): Long {
        return getMillisTillNextRotation(period)
    }

    override fun getTypeId() = ID

    override fun toJson(): JSONObject {
        val obj = super.toJson()
        try {
            obj.put("period", period)
        } catch (e: JSONException) {
            throw java.lang.RuntimeException(e)
        }
        return obj
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TotpInfo) {
            return false
        }
        return super.equals(other) && period == other.period
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + period
        return result
    }

    companion object {
        const val ID: String = "totp"
        const val DEFAULT_PERIOD: Int = 30

        fun getMillisTillNextRotation(period: Int): Long {
            val p = period * 1000L
            return p - (System.currentTimeMillis() % p)
        }
    }
}