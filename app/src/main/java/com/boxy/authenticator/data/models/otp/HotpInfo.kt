package com.boxy.authenticator.data.models.otp

import com.boxy.authenticator.helpers.otp.HOTP.generateOTP
import org.json.JSONException
import org.json.JSONObject
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException

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
        try {
            val otp = generateOTP(secretKey, algorithm, digits, counter)
            return otp.toString()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException(e)
        }
    }

    override fun getTypeId() = ID

    override fun toJson(): JSONObject {
        val obj = super.toJson()
        try {
            obj.put("counter", counter)
        } catch (e: JSONException) {
            throw java.lang.RuntimeException(e)
        }
        return obj
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