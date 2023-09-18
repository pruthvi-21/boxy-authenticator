package com.ps.tokky.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


enum class AccountEntryMethod(val value: String) {
    FORM("form"),
    QR_CODE("qr"),
    RESTORED("restored")
}

enum class OTPType(val value: String) {
    TOTP("totp"),
    HOTP("hotp")
}

object Constants {
    val DEFAULT_OTP_TYPE = OTPType.TOTP
    const val DEFAULT_HASH_ALGORITHM = "SHA1"
    const val DEFAULT_PERIOD = 30
    const val DEFAULT_DIGITS = 6

    const val OTP_GENERATION_REFRESH_INTERVAL = 1000L

    const val BASE32_CHARS = "[A-Z2-7 ]+"

    private const val EXPORT_FILE_NAME_PREFIX = "tokky_accounts_"

    val EXPORT_FILE_NAME: String
        get() {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
            val formatted = currentDateTime.format(formatter)
            return EXPORT_FILE_NAME_PREFIX + formatted
        }

    const val BACKUP_FILE_MIME_TYPE = "text/plain"

    const val DELETE_SUCCESS_RESULT_CODE = 120
}