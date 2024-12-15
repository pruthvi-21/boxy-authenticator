package com.ps.tokky.utils

import android.graphics.Color
import android.net.Uri
import com.ps.tokky.models.TokenEntry
import com.ps.tokky.models.TokenEntry.Companion.KEY_ALGORITHM
import com.ps.tokky.models.TokenEntry.Companion.KEY_DIGITS
import com.ps.tokky.models.TokenEntry.Companion.KEY_ISSUER
import com.ps.tokky.models.TokenEntry.Companion.KEY_LABEL
import com.ps.tokky.models.TokenEntry.Companion.KEY_PERIOD
import com.ps.tokky.models.TokenEntry.Companion.KEY_SECRET_KEY
import com.ps.tokky.models.TokenEntry.Companion.KEY_THUMBNAIL_COLOR
import com.ps.tokky.models.TokenEntry.Companion.KEY_THUMBNAIL_ICON
import com.ps.tokky.models.TokenEntry.Companion.KEY_TYPE
import com.ps.tokky.utils.Constants.DEFAULT_DIGITS
import com.ps.tokky.utils.Constants.DEFAULT_HASH_ALGORITHM
import com.ps.tokky.utils.Constants.DEFAULT_OTP_TYPE
import com.ps.tokky.utils.Constants.DEFAULT_PERIOD
import org.json.JSONObject
import java.util.Date
import java.util.UUID

object TokenBuilder {
    fun buildNewToken(
        issuer: String,
        label: String,
        secretKey: String,
        thumbnailColor: Int = Color.DKGRAY,
        thumbnailIcon: String? = null,
        type: OTPType = DEFAULT_OTP_TYPE,
        algorithm: String = DEFAULT_HASH_ALGORITHM,
        digits: Int = DEFAULT_DIGITS,
        period: Int = DEFAULT_PERIOD,
        addedFrom: AccountEntryMethod
    ): TokenEntry {
        return TokenEntry(
            id = UUID.randomUUID().toString(),
            issuer = issuer,
            label = label,
            secretKey = secretKey,
            thumbnailColor = thumbnailColor,
            thumbnailIcon = thumbnailIcon,
            type = type,
            algorithm = algorithm,
            digits = digits,
            period = period,
            createdOn = Date(),
            updatedOn = Date(),
            addedFrom = addedFrom
        )
    }

    fun buildFromUrl(url: String?): TokenEntry {
        if (url == null)
            throw EmptyURLContentException("URL data is null")

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
        val type = uri.host?.let { OTPType.valueOf(it) } ?: OTPType.TOTP
        val algorithm = params?.get("algorithm") ?: DEFAULT_HASH_ALGORITHM
        val period = params?.get("period")?.toInt() ?: DEFAULT_PERIOD
        val digits = params?.get("digits")?.toInt() ?: DEFAULT_DIGITS

        if (label.startsWith("$issuer:")) label = label.substringAfter("$issuer:")

        return buildNewToken(
            issuer = issuer,
            label = label,
            secretKey = secret,
            type = type,
            algorithm = algorithm,
            digits = digits,
            period = period,
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
        } else DEFAULT_HASH_ALGORITHM

        val thumbnailIcon = json.getString(KEY_THUMBNAIL_ICON)
        val thumbnailColor = json.getInt(KEY_THUMBNAIL_COLOR)

        return buildNewToken(
            issuer = issuer,
            label = label,
            secretKey = secretKey,
            thumbnailColor = thumbnailColor,
            thumbnailIcon = thumbnailIcon,
            type = type,
            algorithm = algorithm,
            digits = digits,
            period = period,
            addedFrom = AccountEntryMethod.RESTORED
        )
    }
}
