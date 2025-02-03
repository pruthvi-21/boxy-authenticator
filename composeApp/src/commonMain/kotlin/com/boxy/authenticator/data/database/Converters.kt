package com.boxy.authenticator.data.database

import androidx.room.TypeConverter
import com.boxy.authenticator.data.models.Thumbnail
import com.boxy.authenticator.data.models.otp.OtpInfo
import com.boxy.authenticator.helpers.Logger
import com.boxy.authenticator.helpers.serializers.BoxyJson
import com.boxy.authenticator.utils.AccountEntryMethod
import com.boxy.authenticator.utils.OTPType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class Converters {

    @TypeConverter
    fun fromOtpType(otpType: OTPType): String {
        return otpType.name
    }

    @TypeConverter
    fun toOtpType(otpTypeValue: String): OTPType {
        return OTPType.valueOf(otpTypeValue)
    }

    @TypeConverter
    fun fromAccountEntryMethod(via: AccountEntryMethod): String {
        return via.name
    }

    @TypeConverter
    fun toAccountEntryMethod(viaValue: String): AccountEntryMethod {
        return AccountEntryMethod.valueOf(viaValue)
    }

    @TypeConverter
    fun fromJsonObject(jsonObject: JsonObject): String {
        return jsonObject.toString()
    }

    @TypeConverter
    fun toJsonObject(json: String): JsonObject {
        return BoxyJson.parseToJsonElement(json).jsonObject
    }

    @TypeConverter
    fun fromThumbnail(thumbnail: Thumbnail): String {
        return thumbnail.toString()
    }

    @TypeConverter
    fun toThumbnail(thumbnailString: String): Thumbnail {
        return Thumbnail.fromString(thumbnailString)
    }

    @TypeConverter
    fun fromOtpInfo(otpInfo: OtpInfo): JsonObject {
        return otpInfo.toJson()
    }

    @TypeConverter
    fun toOtpInfo(otpInfoJson: JsonObject): OtpInfo {
        return OtpInfo.fromJson(otpInfoJson)
    }
}