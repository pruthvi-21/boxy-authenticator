package com.boxy.authenticator.data.database

import com.boxy.authenticator.data.models.Thumbnail
import com.boxy.authenticator.data.models.otp.OtpInfo
import com.boxy.authenticator.helpers.serializers.BoxyJson
import com.boxy.authenticator.utils.AccountEntryMethod
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

object Converters {

    fun fromAccountEntryMethod(via: AccountEntryMethod): String {
        return via.name
    }

    fun toAccountEntryMethod(viaValue: String): AccountEntryMethod {
        return AccountEntryMethod.valueOf(viaValue)
    }

    fun fromJsonObject(jsonObject: JsonObject): String {
        return jsonObject.toString()
    }

    fun toJsonObject(json: String): JsonObject {
        return BoxyJson.parseToJsonElement(json).jsonObject
    }

    fun fromThumbnail(thumbnail: Thumbnail): String {
        return BoxyJson.encodeToString(thumbnail)
    }

    fun toThumbnail(thumbnailString: String): Thumbnail {
        return Thumbnail.fromString(thumbnailString)
    }

    fun fromOtpInfo(otpInfo: OtpInfo): JsonObject {
        return otpInfo.toJson()
    }

    fun toOtpInfo(otpInfoJson: JsonObject): OtpInfo {
        return OtpInfo.fromJson(otpInfoJson)
    }
}