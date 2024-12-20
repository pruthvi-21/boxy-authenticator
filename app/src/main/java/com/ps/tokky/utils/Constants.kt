package com.ps.tokky.utils

import android.graphics.Color
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class AccountEntryMethod {
    FORM, QR_CODE, RESTORED,
}

enum class OTPType {
    TOTP, HOTP,
}

enum class HashAlgorithm {
    SHA1, SHA256, SHA512
}

object Constants {
    val DEFAULT_OTP_TYPE = OTPType.TOTP
    val DEFAULT_HASH_ALGORITHM = HashAlgorithm.SHA1
    const val DEFAULT_PERIOD = 30
    const val DEFAULT_DIGITS = 6

    const val DIGITS_MIN_VALUE = 1
    const val DIGITS_MAX_VALUE = 10

    val THUMBNAIL_COlORS = listOf(
        Color.parseColor("#A0522D"),
        Color.parseColor("#376B97"),
        Color.parseColor("#556B2F"),
        Color.parseColor("#B18F96"),
        Color.parseColor("#C8AA4B"),
    )

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

    const val LOGIN_PIN_LENGTH = 4
}