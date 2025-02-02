package com.boxy.authenticator.data.models.otp

import com.boxy.authenticator.helpers.otp.HOTP
import com.boxy.authenticator.helpers.serializers.ByteArraySerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("hotp")
@OptIn(ExperimentalSerializationApi::class)
class HotpInfo(
    @Serializable(with = ByteArraySerializer::class)
    override var secretKey: ByteArray,
    @EncodeDefault
    override var algorithm: String = DEFAULT_ALGORITHM,
    @EncodeDefault
    override var digits: Int = DEFAULT_DIGITS,
    @EncodeDefault
    var counter: Long = DEFAULT_COUNTER,
) : OtpInfo() {

    fun incrementCounter() {
        counter++
    }

    override fun getOtp(): String {
        val otp = HOTP.generateOTP(secretKey, algorithm, digits, counter)
        return otp.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is HotpInfo) return false

        return super.equals(other)
                && counter == other.counter
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + counter.hashCode()
        return result
    }

    companion object {
        const val DEFAULT_COUNTER: Long = 0L
        const val COUNTER_MIN_VALUE = 0
    }
}