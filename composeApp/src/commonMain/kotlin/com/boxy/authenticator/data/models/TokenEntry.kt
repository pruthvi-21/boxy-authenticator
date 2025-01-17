package com.boxy.authenticator.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.boxy.authenticator.data.database.ThumbnailConverter
import com.boxy.authenticator.data.models.otp.HotpInfo
import com.boxy.authenticator.data.models.otp.HotpInfo.Companion.DEFAULT_COUNTER
import com.boxy.authenticator.data.models.otp.OtpInfo
import com.boxy.authenticator.data.models.otp.OtpInfo.Companion.DEFAULT_ALGORITHM
import com.boxy.authenticator.data.models.otp.OtpInfo.Companion.DEFAULT_DIGITS
import com.boxy.authenticator.data.models.otp.SteamInfo
import com.boxy.authenticator.data.models.otp.TotpInfo
import com.boxy.authenticator.data.models.otp.TotpInfo.Companion.DEFAULT_PERIOD
import com.boxy.authenticator.utils.AccountEntryMethod
import com.boxy.authenticator.utils.BadlyFormedURLException
import com.boxy.authenticator.utils.Base32
import com.boxy.authenticator.utils.Constants
import com.boxy.authenticator.utils.Constants.DEFAULT_OTP_TYPE
import com.boxy.authenticator.utils.EmptyURLContentException
import com.boxy.authenticator.utils.OTPType
import com.boxy.authenticator.utils.Utils
import com.boxy.authenticator.utils.cleanSecretKey
import io.ktor.http.Url
import io.ktor.http.decodeURLPart
import io.ktor.util.flattenEntries
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(tableName = "token_entry")
data class TokenEntry(
    @PrimaryKey val id: String,
    var issuer: String,
    var label: String,
    var thumbnail: Thumbnail,
    val type: OTPType,
    val otpInfo: OtpInfo,
    val createdOn: Long,
    var updatedOn: Long,
    val addedFrom: AccountEntryMethod,
) {

    fun toJson(): JsonObject {
        val json = buildJsonObject {
            put("issuer", issuer)
            put("label", label)
            put("thumbnail", ThumbnailConverter.fromThumbnail(thumbnail))
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

        @OptIn(ExperimentalUuidApi::class)
        fun buildNewToken(
            issuer: String,
            label: String,
            thumbnail: Thumbnail = Thumbnail.Color(Constants.THUMBNAIL_COlORS.random()),
            type: OTPType = DEFAULT_OTP_TYPE,
            otpInfo: OtpInfo,
            addedFrom: AccountEntryMethod,
        ): TokenEntry {
            return TokenEntry(
                id = Uuid.random().toString(),
                issuer = issuer,
                label = label,
                thumbnail = thumbnail,
                type = type,
                otpInfo = otpInfo,
                createdOn = Clock.System.now().toEpochMilliseconds(),
                updatedOn = Clock.System.now().toEpochMilliseconds(),
                addedFrom = addedFrom
            )
        }

        fun buildFromUrl(url: String?): TokenEntry {
            if (url.isNullOrEmpty())
                throw EmptyURLContentException("URL data is null or empty")

            val uri = Url(url)

            if (!Utils.isValidTOTPAuthURL(uri.toString())) {
                throw BadlyFormedURLException("Invalid URL format")
            }

            val params = uri.parameters.flattenEntries()
                .associate { it.first to it.second.decodeURLPart() }

            val type = uri.host.let { OTPType.valueOf(it.uppercase()) }

            val issuer = params["issuer"] ?: ""
            var label = uri.encodedPath.substring(1)

            if (label.startsWith("$issuer:")) label = label.substringAfter("$issuer:")

            val secret = params["secret"]?.cleanSecretKey() ?: ""
            val secretDecoded = Base32.decode(secret)
            val algorithm = params["algorithm"] ?: DEFAULT_ALGORITHM
            val digits = params["digits"]?.toInt() ?: DEFAULT_DIGITS

            val otpInfo = when (type) {
                OTPType.TOTP -> {
                    val period = params["period"]?.toInt() ?: DEFAULT_PERIOD
                    TotpInfo(secretDecoded, algorithm, digits, period)
                }

                OTPType.HOTP -> {
                    val counter = params["counter"]?.toLong() ?: DEFAULT_COUNTER
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

        fun buildFromExportJson(json: JsonObject): TokenEntry {
            val issuer = json["issuer"]?.jsonPrimitive?.content
            val label = json["label"]?.jsonPrimitive?.content

//            val thumbnail =
//                ThumbnailConverter.toThumbnail(json["thumbnail"]?.jsonPrimitive?.content)

            val type =
                OTPType.valueOf(json["type"]?.jsonPrimitive?.content ?: DEFAULT_OTP_TYPE.name)

            val otpInfo = OtpInfo.fromJson(
                Json.parseToJsonElement(
                    json["otp_info"]?.jsonPrimitive?.content ?: error("Missing otp_info")
                )
                    .jsonObject
            )
            return buildNewToken(
                issuer = issuer!!,
                label = label!!,
//                thumbnail = thumbnail,
                type = type,
                otpInfo = otpInfo,
                addedFrom = AccountEntryMethod.RESTORED
            )
        }
    }
}
