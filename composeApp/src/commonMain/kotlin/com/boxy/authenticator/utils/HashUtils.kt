package com.boxy.authenticator.utils

import diglol.crypto.Hmac
import diglol.crypto.Pbkdf2
import diglol.crypto.random.nextBytes
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64

object HashUtils {
    private const val SALT_LENGTH = 16
    private const val ITERATIONS = 10000

    suspend fun hash(passcode: String): String {
        val salt = generateSalt()
        val hashedPassword = hashWithPBKDF2(passcode.encodeToByteArray(), salt)
        val saltBase64 = salt.encodeBase64()
        val hashBase64 = hashedPassword.encodeBase64()

        return "$saltBase64:$hashBase64"
    }

    suspend fun verifyHash(passcode: String, hash: String): Boolean {
        val parts = hash.split(":")
        if (parts.size != 2) return false

        val salt = parts[0].decodeBase64Bytes()
        val storedHash = parts[1].decodeBase64Bytes()
        val computedHash = hashWithPBKDF2(passcode.encodeToByteArray(), salt)

        return storedHash.contentEquals(computedHash)
    }

    private fun generateSalt(): ByteArray {
        return nextBytes(SALT_LENGTH)
    }

    private suspend fun hashWithPBKDF2(passcode: ByteArray, salt: ByteArray): ByteArray {
        val algorithm = Pbkdf2(hmacType = Hmac.Type.SHA256, iterations = ITERATIONS)
        return algorithm.deriveKey(
            password = passcode,
            salt = salt,
        )
    }
}