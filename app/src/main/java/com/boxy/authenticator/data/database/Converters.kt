package com.boxy.authenticator.data.database

import androidx.room.TypeConverter
import com.boxy.authenticator.data.models.otp.OtpInfo
import com.boxy.authenticator.utils.AccountEntryMethod
import com.boxy.authenticator.utils.OTPType
import org.json.JSONObject
import java.util.Date

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
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun fromOtpInfo(otpInfo: OtpInfo): String {
        return otpInfo.toJson().toString()
    }

    @TypeConverter
    fun toOtpInfo(otpInfoJson: String): OtpInfo {
        return OtpInfo.fromJson(JSONObject(otpInfoJson))
    }
}