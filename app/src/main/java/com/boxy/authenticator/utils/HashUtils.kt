package com.boxy.authenticator.utils

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object HashUtils {
    private const val SALT_LENGTH = 16
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"

    fun hash(passcode: String): String {
        val salt = generateSalt()
        val hashedPassword = hashWithPBKDF2(passcode, salt)
        return "$salt:$hashedPassword"
    }

    fun verifyString(value: String, hash: String): Boolean {
        val parts = hash.split(":")
        if (parts.size != 2) { return false }

        val salt = parts[0]
        val storedHash = parts[1]
        val enteredHash = hashWithPBKDF2(value, salt)
        return storedHash == enteredHash
    }

    private fun generateSalt(): String {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    private fun hashWithPBKDF2(passcode: String, salt: String): String {
        val spec = PBEKeySpec(
            passcode.toCharArray(),
            Base64.getDecoder().decode(salt),
            ITERATIONS,
            KEY_LENGTH
        )
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        val hash = factory.generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(hash)
    }
}