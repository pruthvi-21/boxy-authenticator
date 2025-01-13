package com.boxy.authenticator.data.models.otp

import com.boxy.authenticator.utils.Base32
import com.boxy.authenticator.utils.EncodingException
import com.boxy.authenticator.utils.OtpInfoException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.put
import kotlin.jvm.JvmOverloads

abstract class OtpInfo @JvmOverloads constructor(
    var secretKey: ByteArray,
    var algorithm: String = DEFAULT_ALGORITHM,
    var digits: Int = DEFAULT_DIGITS,
) {

    @Throws(OtpInfoException::class)
    abstract fun getOtp(): String

    abstract fun getTypeId(): String

    open fun toJson(): JsonObject {
        return try {
            buildJsonObject {
                put("type", getTypeId())
                put(
                    "secret",
                    Base32.encode(secretKey)
                ) // You may need a KMP compatible Base32 encoder
                put("algo", algorithm)
                put("digits", digits)
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is OtpInfo) {
            return false
        }

        return getTypeId() == other.getTypeId()
                && secretKey.contentEquals(other.secretKey) && algorithm == other.algorithm
                && digits == other.digits
    }

    override fun hashCode(): Int {
        var result = secretKey.contentHashCode()
        result = 31 * result + algorithm.hashCode()
        result = 31 * result + digits
        return result
    }

    companion object {
        const val DEFAULT_DIGITS: Int = 6
        const val DEFAULT_ALGORITHM: String = "SHA1"

        @Throws(OtpInfoException::class)
        fun fromJson(obj: JsonObject): OtpInfo {
            return try {
                val type = obj["type"]?.jsonPrimitive?.content
                    ?: throw OtpInfoException("Type missing")
                val secret = Base32.decode(
                    obj["secret"]?.jsonPrimitive?.content
                        ?: throw OtpInfoException("Secret missing")
                )

                when (type) {
                    TotpInfo.ID -> {
                        val algo = obj["algo"]?.jsonPrimitive?.content
                            ?: throw OtpInfoException("Algorithm missing")
                        val digits = obj["digits"]?.jsonPrimitive?.int
                            ?: throw OtpInfoException("Digits missing")
                        val period = obj["period"]?.jsonPrimitive?.int
                            ?: throw OtpInfoException("Period missing")
                        TotpInfo(secret, algo, digits, period)
                    }

                    HotpInfo.ID -> {
                        val algo = obj["algo"]?.jsonPrimitive?.content
                            ?: throw OtpInfoException("Algorithm missing")
                        val digits = obj["digits"]?.jsonPrimitive?.int
                            ?: throw OtpInfoException("Digits missing")
                        val counter = obj["counter"]?.jsonPrimitive?.long
                            ?: throw OtpInfoException("Counter missing")
                        HotpInfo(secret, algo, digits, counter)
                    }

                    SteamInfo.ID -> SteamInfo(secret)
                    else -> throw OtpInfoException("Unsupported OTP type: $type")
                }
            } catch (e: EncodingException) {
                throw OtpInfoException("Encoding exception: ${e.message}")
            } catch (e: Exception) {
                throw OtpInfoException("JSON Parsing exception: ${e.message}")
            }
        }
    }
}