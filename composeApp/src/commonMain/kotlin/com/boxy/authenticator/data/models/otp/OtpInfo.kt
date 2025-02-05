package com.boxy.authenticator.data.models.otp

import com.boxy.authenticator.helpers.serializers.BoxyJson
import com.boxy.authenticator.helpers.serializers.ByteArraySerializer
import com.boxy.authenticator.utils.OtpInfoException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
sealed class OtpInfo {
    @Serializable(with = ByteArraySerializer::class)
    abstract var secretKey: ByteArray
    abstract var algorithm: String
    abstract var digits: Int

    @Throws(OtpInfoException::class)
    abstract fun getOtp(): String

    open fun serialize(): String {
        return try {
            BoxyJson.encodeToString(this)
        } catch (e: SerializationException) {
            throw SerializationException("Failed to serialize OtpInfo", e)
        }
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

        fun deserialize(jsonString: String): OtpInfo {
            return try {
                BoxyJson.decodeFromString(jsonString)
            } catch (e: SerializationException) {
                throw SerializationException("Failed to deserialize OtpInfo", e)
            }
        }
    }
}