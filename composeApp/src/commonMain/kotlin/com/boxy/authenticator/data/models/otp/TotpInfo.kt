package com.boxy.authenticator.data.models.otp

import com.boxy.authenticator.helpers.otp.TOTP
import kotlinx.datetime.Clock
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.jvm.JvmOverloads

open class TotpInfo @JvmOverloads constructor(
    secretKey: ByteArray,
    algorithm: String = DEFAULT_ALGORITHM,
    digits: Int = DEFAULT_DIGITS,
    var period: Int = DEFAULT_PERIOD,
) : OtpInfo(secretKey, algorithm, digits) {

    override fun getOtp(): String {
        return getOtp(Clock.System.now().toEpochMilliseconds() / 1000)
    }

    open fun getOtp(time: Long): String {
        val otp = TOTP.generateOTP(secretKey, algorithm, digits, period.toLong(), time)
        return otp.toString()
    }

    fun getMillisTillNextRotation(): Long {
        return getMillisTillNextRotation(period)
    }

    override fun getTypeId() = ID

    override fun toJson(): JsonObject {
        val obj = super.toJson()
        return try {
            buildJsonObject {
                obj.forEach { put(it.key, it.value) }

                put("period", period)
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
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
            return p - (Clock.System.now().toEpochMilliseconds() % p)
        }
    }
}