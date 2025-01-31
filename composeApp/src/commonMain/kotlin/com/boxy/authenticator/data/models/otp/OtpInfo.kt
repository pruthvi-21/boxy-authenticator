package com.boxy.authenticator.data.models.otp

import com.boxy.authenticator.helpers.serializers.BoxyJson
import com.boxy.authenticator.utils.OtpInfoException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
sealed class OtpInfo {
    abstract var secretKey: ByteArray
    abstract var algorithm: String
    abstract var digits: Int

    @Throws(OtpInfoException::class)
    abstract fun getOtp(): String

    open fun toJson(): JsonObject {
        return BoxyJson.encodeToJsonElement(this).jsonObject
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OtpInfo) return false

        return secretKey.contentEquals(other.secretKey)
                && algorithm == other.algorithm
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

        fun fromJson(jsonObject: JsonObject): OtpInfo {
            return BoxyJson.decodeFromJsonElement(jsonObject)
        }
    }
}