package com.ps.tokky.utils

object CryptoUtils {

    fun hashPasscode(passcode: String): String {
        return passcode.hash("SHA256").hash("SHA1")
    }
}