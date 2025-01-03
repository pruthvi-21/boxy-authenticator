package com.ps.tokky.data.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ps.tokky.data.models.otp.HotpInfo
import com.ps.tokky.data.models.otp.HotpInfo.Companion.DEFAULT_COUNTER
import com.ps.tokky.data.models.otp.OtpInfo
import com.ps.tokky.data.models.otp.OtpInfo.Companion.DEFAULT_ALGORITHM
import com.ps.tokky.data.models.otp.OtpInfo.Companion.DEFAULT_DIGITS
import com.ps.tokky.data.models.otp.SteamInfo
import com.ps.tokky.data.models.otp.TotpInfo
import com.ps.tokky.data.models.otp.TotpInfo.Companion.DEFAULT_PERIOD
import com.ps.tokky.utils.AccountEntryMethod
import com.ps.tokky.utils.BadlyFormedURLException
import com.ps.tokky.utils.Base32
import com.ps.tokky.utils.Constants
import com.ps.tokky.utils.Constants.DEFAULT_OTP_TYPE
import com.ps.tokky.utils.EmptyURLContentException
import com.ps.tokky.utils.OTPType
import com.ps.tokky.utils.Utils
import com.ps.tokky.utils.cleanSecretKey
import org.json.JSONObject
import java.util.Date
import java.util.UUID

@Entity(tableName = "token_entry")
data class TokenEntry(
    @PrimaryKey val id: String,
    var issuer: String,
    var label: String,
    var thumbnailColor: Int,
    var thumbnailIcon: String? = null,
    val type: OTPType,
    val otpInfo: OtpInfo,
    val createdOn: Date,
    var updatedOn: Date,
    val addedFrom: AccountEntryMethod,
) {

    fun toJson(): JSONObject {
        val json = JSONObject().apply {
            put("issuer", issuer)
            put("label", label)
            put("thumbnail_color", thumbnailColor)
            put("thumbnail_icon", thumbnailIcon)
            put("type", type.name)
            put("otp_info", otpInfo.toJson())
        }
        return json
    }

    val name: String
        get() {
            if (label.isEmpty()) return issuer
            return "$issuer ($label)"
        }

    companion object {
        const val TAG = "TokenEntry"

        fun buildNewToken(
            issuer: String,
            label: String,
            thumbnailColor: Int = Constants.THUMBNAIL_COlORS.random(),
            thumbnailIcon: String? = null,
            type: OTPType = DEFAULT_OTP_TYPE,
            otpInfo: OtpInfo,
            addedFrom: AccountEntryMethod,
        ): TokenEntry {
            return TokenEntry(
                id = UUID.randomUUID().toString(),
                issuer = issuer,
                label = label,
                thumbnailColor = thumbnailColor,
                thumbnailIcon = thumbnailIcon,
                type = type,
                otpInfo = otpInfo,
                createdOn = Date(),
                updatedOn = Date(),
                addedFrom = addedFrom
            )
        }

        fun buildFromUrl(url: String?): TokenEntry {
            if (url.isNullOrEmpty())
                throw EmptyURLContentException("URL data is null or empty")

            val uri = Uri.parse(url)

            if (!Utils.isValidTOTPAuthURL(uri.toString())) {
                throw BadlyFormedURLException("Invalid URL format")
            }

            val params = uri.query?.split("&")
                ?.associate {
                    it.split("=")
                        .let { pair -> pair[0] to pair[1] }
                }

            val type = uri.host?.let { OTPType.valueOf(it.uppercase()) } ?: OTPType.TOTP

            val issuer = params?.get("issuer") ?: ""
            var label = uri.path?.substring(1) ?: ""

            if (label.startsWith("$issuer:")) label = label.substringAfter("$issuer:")

            val secret = params?.get("secret")?.cleanSecretKey() ?: ""
            val secretDecoded = Base32.decode(secret)
            val algorithm = params?.get("algorithm") ?: DEFAULT_ALGORITHM
            val digits = params?.get("digits")?.toInt() ?: DEFAULT_DIGITS

            val otpInfo = when (type) {
                OTPType.TOTP -> {
                    val period = params?.get("period")?.toInt() ?: DEFAULT_PERIOD
                    TotpInfo(secretDecoded, algorithm, digits, period)
                }

                OTPType.HOTP -> {
                    val counter = params?.get("counter")?.toLong() ?: DEFAULT_COUNTER
                    HotpInfo(secretDecoded, algorithm, digits, counter)
                }

                OTPType.STEAM -> {
                    SteamInfo(secretDecoded)
                }
            }

            return buildNewToken(
                issuer = issuer,
                label = label,
                type = type,
                otpInfo = otpInfo,
                addedFrom = AccountEntryMethod.QR_CODE
            )
        }

        fun buildFromExportJson(json: JSONObject): TokenEntry {
            val issuer = json.getString("issuer")
            val label = json.getString("label")

            val thumbnailColor = json.getInt("thumbnail_color")
            val thumbnailIcon = if (json.has("thumbnail_icon")) json.getString("thumbnail_icon")
            else null

            val type = if (json.has("type")) OTPType.valueOf(json.getString("type"))
            else DEFAULT_OTP_TYPE

            val otpInfo = OtpInfo.fromJson(JSONObject(json.getString("otp_info")))

            return buildNewToken(
                issuer = issuer,
                label = label,
                thumbnailColor = thumbnailColor,
                thumbnailIcon = thumbnailIcon,
                type = type,
                otpInfo = otpInfo,
                addedFrom = AccountEntryMethod.RESTORED
            )
        }
    }
}
