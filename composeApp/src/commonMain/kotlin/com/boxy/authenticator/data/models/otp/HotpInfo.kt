package com.boxy.authenticator.data.models.otp

import com.boxy.authenticator.helpers.otp.HOTP
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("hotp")
class HotpInfo(
    override var secretKey: ByteArray,
    override var algorithm: String = DEFAULT_ALGORITHM,
    override var digits: Int = DEFAULT_DIGITS,
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