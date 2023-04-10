package com.ps.tokky.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Constants {
    const val DEFAULT_OTP_VALIDITY = 30
    const val DEFAULT_OTP_LENGTH = 6
    const val DEFAULT_HASH_ALGORITHM = "SHA1"

    const val OTP_GENERATION_REFRESH_INTERVAL = 1000L

    const val BASE32_CHARS = "[A-Z2-7 ]+"

    const val KEY_LIST_ORDER = "list_order"

    private const val EXPORT_FILE_NAME_PREFIX = "tokky_accounts_"

    val EXPORT_FILE_NAME: String
        get() {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
            val formatted = currentDateTime.format(formatter)
            return EXPORT_FILE_NAME_PREFIX + formatted
        }

    const val FILE_MIME_TYPE = "application/json"
}