package com.ps.tokky.database

import androidx.room.TypeConverter
import com.ps.tokky.utils.AccountEntryMethod
import com.ps.tokky.utils.OTPType
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
}