package com.boxy.authenticator.data.models.otp

import com.boxy.authenticator.helpers.otp.TOTP
import com.boxy.authenticator.helpers.serializers.ByteArraySerializer
import kotlinx.datetime.Clock
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("totp")
@OptIn(ExperimentalSerializationApi::class)
open class TotpInfo(
    @Serializable(with = ByteArraySerializer::class)
    override var secretKey: ByteArray,
    @EncodeDefault
    override var algorithm: String = DEFAULT_ALGORITHM,
    @EncodeDefault
    override var digits: Int = DEFAULT_DIGITS,
    @EncodeDefault
    var period: Int = DEFAULT_PERIOD,
) : OtpInfo() {

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

    override fun equals(other: Any?): Boolean {
        if (other !is TotpInfo) return false

        return super.equals(other)
                && period == other.period
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + period
        return result
    }

    companion object {
        const val DEFAULT_PERIOD: Int = 30

        fun getMillisTillNextRotation(period: Int): Long {
            val p = period * 1000L
            return p - (Clock.System.now().toEpochMilliseconds() % p)
        }
    }
}