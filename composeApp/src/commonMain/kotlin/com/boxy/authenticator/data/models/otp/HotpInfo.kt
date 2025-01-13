package com.boxy.authenticator.data.models.otp

import com.boxy.authenticator.helpers.otp.HOTP
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.jvm.JvmOverloads

class HotpInfo @JvmOverloads constructor(
    secretKey: ByteArray,
    algorithm: String = DEFAULT_ALGORITHM,
    digits: Int = DEFAULT_DIGITS,
    var counter: Long = DEFAULT_COUNTER,
) : OtpInfo(secretKey, algorithm, digits) {

    fun incrementCounter() {
        counter++
    }

    override fun getOtp(): String {
        val otp = HOTP.generateOTP(secretKey, algorithm, digits, counter)
        return otp.toString()
    }

    override fun getTypeId() = ID

    override fun toJson(): JsonObject {
        val obj = super.toJson()
        return try {
            buildJsonObject {
                obj.forEach { put(it.key, it.value) }
                put("counter", counter)
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }


    override fun equals(other: Any?): Boolean {
        if (other !is HotpInfo) {
            return false
        }

        return super.equals(other) && counter == other.counter
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + counter.hashCode()
        return result
    }

    companion object {
        const val ID: String = "hotp"
        const val DEFAULT_COUNTER: Long = 0L
        const val COUNTER_MIN_VALUE = 0
    }
}