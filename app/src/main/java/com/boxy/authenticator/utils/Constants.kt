package com.boxy.authenticator.utils

import android.graphics.Color
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class AccountEntryMethod {
    FORM, QR_CODE, RESTORED,
}

enum class OTPType {
    TOTP, HOTP, STEAM,
}

enum class TokenSetupMode {
    NEW, URL, UPDATE
}

object Constants {
    val DEFAULT_OTP_TYPE = OTPType.TOTP

    val THUMBNAIL_COlORS = listOf(
        Color.parseColor("#A0522D"),
        Color.parseColor("#376B97"),
        Color.parseColor("#556B2F"),
        Color.parseColor("#B18F96"),
        Color.parseColor("#C8AA4B"),
    )

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