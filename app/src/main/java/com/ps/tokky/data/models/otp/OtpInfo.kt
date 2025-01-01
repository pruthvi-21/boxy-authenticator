package com.ps.tokky.data.models.otp

import com.ps.tokky.utils.Base32.decode
import com.ps.tokky.utils.Base32.encode
import com.ps.tokky.utils.EncodingException
import com.ps.tokky.utils.OtpInfoException
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

abstract class OtpInfo @JvmOverloads constructor(
    var secretKey: ByteArray,
    var algorithm: String = DEFAULT_ALGORITHM,
    var digits: Int = DEFAULT_DIGITS,
) : Serializable {

    @Throws(OtpInfoException::class)
    abstract fun getOtp(): String

    abstract fun getTypeId(): String

    open fun toJson(): JSONObject {
        val obj = JSONObject()

        try {
            obj.put("type", getTypeId())
            obj.put("secret", encode(secretKey))
            obj.put("algo", algorithm)
            obj.put("digits", digits)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }

        return obj
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
        fun fromJson(obj: JSONObject): OtpInfo {
            val info: OtpInfo

            try {
                val type = obj.getString("type")
                val secret = decode(obj.getString("secret"))

                info = when (type) {
                    TotpInfo.ID -> {
                        val algo = obj.getString("algo")
                        val digits = obj.getInt("digits")
                        val period = obj.getInt("period")
                        TotpInfo(secret, algo, digits, period)
                    }
                    HotpInfo.ID -> {
                        val algo = obj.getString("algo")
                        val digits = obj.getInt("digits")
                        val counter = obj.getLong("counter")
                        HotpInfo(secret, algo, digits, counter)
                    }
                    SteamInfo.ID -> SteamInfo(secret)
                    else -> throw OtpInfoException("unsupported otp type: $type")
                }
            } catch (e: EncodingException) {
                throw OtpInfoException(e)
            } catch (e: JSONException) {
                throw OtpInfoException(e)
            }

            return info
        }
    }
}