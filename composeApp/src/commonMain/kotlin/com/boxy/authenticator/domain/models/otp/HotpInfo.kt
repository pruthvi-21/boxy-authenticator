package com.boxy.authenticator.domain.models.otp

import com.boxy.authenticator.core.otp.OtpGenerator
import com.boxy.authenticator.core.serialization.ByteArraySerializer
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
        val otp = OtpGenerator.generateHotp(secretKey, algorithm, counter)
        return "$otp".takeLast(digits).padStart(digits, '0')
    }

    companion object {
        const val DEFAULT_COUNTER: Long = 0L
        const val COUNTER_MIN_VALUE: Long = 0L
    }
}