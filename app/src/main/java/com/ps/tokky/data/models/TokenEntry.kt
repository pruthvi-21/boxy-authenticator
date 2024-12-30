package com.ps.tokky.data.models

import android.graphics.Color
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ps.tokky.data.models.otp.OtpInfo
import com.ps.tokky.data.models.otp.OtpInfo.Companion.DEFAULT_ALGORITHM
import com.ps.tokky.data.models.otp.OtpInfo.Companion.DEFAULT_DIGITS
import com.ps.tokky.data.models.otp.TotpInfo
import com.ps.tokky.data.models.otp.TotpInfo.Companion.DEFAULT_PERIOD
import com.ps.tokky.utils.AccountEntryMethod
import com.ps.tokky.utils.BadlyFormedURLException
import com.ps.tokky.utils.Base32
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

    val name: String
        get() {
            if (label.isEmpty()) return issuer
            return "$issuer ($label)"
        }

    companion object {
        const val TAG = "TokenEntry"

        const val KEY_ISSUER = "issuer"
        const val KEY_LABEL = "label"
        const val KEY_SECRET_KEY = "secret_key"
        const val KEY_THUMBNAIL_COLOR = "thumbnail_color"
        const val KEY_THUMBNAIL_ICON = "thumbnail_icon"
        const val KEY_TYPE = "type"
        const val KEY_ALGORITHM = "algorithm"
        const val KEY_PERIOD = "period"
        const val KEY_DIGITS = "digits"

        fun buildNewToken(
            issuer: String,
            label: String,
            thumbnailColor: Int = Color.DKGRAY,
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

            val issuer = params?.get("issuer") ?: ""
            var label = uri.path?.substring(1) ?: ""
            val secret = params?.get("secret")?.cleanSecretKey() ?: ""
            val type = uri.host?.let { OTPType.valueOf(it.uppercase()) } ?: OTPType.TOTP
            val algorithm = params?.get("algorithm") ?: DEFAULT_ALGORITHM
            val period = params?.get("period")?.toInt() ?: DEFAULT_PERIOD
            val digits = params?.get("digits")?.toInt() ?: DEFAULT_DIGITS

            if (label.startsWith("$issuer:")) label = label.substringAfter("$issuer:")

            return buildNewToken(
                issuer = issuer,
                label = label,
                type = type,
                otpInfo = TotpInfo(Base32.decode(secret), algorithm, digits, period),
                addedFrom = AccountEntryMethod.QR_CODE
            )
        }

        fun buildFromExportJson(json: JSONObject): TokenEntry {
            val issuer = json.getString(KEY_ISSUER)
            val label = json.getString(KEY_LABEL)
            val secretKey = json.getString(KEY_SECRET_KEY)

            val type = if (json.has(KEY_TYPE)) OTPType.valueOf(json.getString(KEY_TYPE))
            else DEFAULT_OTP_TYPE

            val period = if (json.has(KEY_PERIOD)) json.getInt(KEY_PERIOD)
            else DEFAULT_PERIOD

            val digits = if (json.has(KEY_DIGITS)) json.getInt(KEY_DIGITS)
            else DEFAULT_DIGITS

            val algorithm = if (json.has(KEY_ALGORITHM)) {
                json.getString(KEY_ALGORITHM)
            } else DEFAULT_ALGORITHM

            val thumbnailIcon = if (json.has(KEY_THUMBNAIL_ICON)) json.getString(KEY_THUMBNAIL_ICON)
            else null
            val thumbnailColor = json.getInt(KEY_THUMBNAIL_COLOR)

            return buildNewToken(
                issuer = issuer,
                label = label,
                thumbnailColor = thumbnailColor,
                thumbnailIcon = thumbnailIcon,
                type = type,
                otpInfo = TotpInfo(Base32.decode(secretKey), algorithm, digits, period),
                addedFrom = AccountEntryMethod.RESTORED
            )
        }
    }
}
