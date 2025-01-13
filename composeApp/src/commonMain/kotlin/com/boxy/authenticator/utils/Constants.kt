package com.boxy.authenticator.utils

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

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
        "#A0522D",
        "#376B97",
        "#556B2F",
        "#B18F96",
        "#C8AA4B",
    )

    private const val EXPORT_FILE_NAME_PREFIX = "tokky_accounts_"
}