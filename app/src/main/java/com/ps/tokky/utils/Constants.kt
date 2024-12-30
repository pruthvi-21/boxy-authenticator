package com.ps.tokky.utils

import android.graphics.Color
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

enum class AccountEntryMethod {
    FORM, QR_CODE, RESTORED,
}

enum class OTPType {
    TOTP, HOTP,
}

object Constants {
    val DEFAULT_OTP_TYPE = OTPType.TOTP

    const val DIGITS_MIN_VALUE = 1
    const val DIGITS_MAX_VALUE = 10

    val THUMBNAIL_COlORS = listOf(
        Color.parseColor("#A0522D"),
        Color.parseColor("#376B97"),
        Color.parseColor("#556B2F"),
        Color.parseColor("#B18F96"),
        Color.parseColor("#C8AA4B"),
    )

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
}