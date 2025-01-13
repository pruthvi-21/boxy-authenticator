package com.boxy.authenticator.helpers

import com.boxy.authenticator.data.models.otp.HotpInfo.Companion.COUNTER_MIN_VALUE
import com.boxy.authenticator.utils.Base32

class TokenFormValidator() {

    data class Result(
        val isValid: Boolean = false,
        val errorMessage: String? = null,
    )

    fun validateIssuer(issuer: String): Result {
        return if (issuer.isNotEmpty()) {
            Result(true)
        } else {
            Result(false, "Issuer cannot be empty")
        }
    }

    fun validateSecretKey(secretKey: String): Result {
        return try {
            val decoded = Base32.decode(secretKey)
            if (decoded.isEmpty()) {
                Result(false, "Secret Key cannot be empty")
            } else {
                Result(true)
            }
        } catch (e: Exception) {
            Result(false, "Invalid Secret Key format")
        }
    }

    fun validatePeriod(period: String): Result {
        val errorMessage = when {
            period.isEmpty() -> "Period cannot be empty"
            period.toIntOrNull() == null || period.toInt() == 0 -> "Period must be a positive number >0"
            else -> null
        }

        return if (errorMessage == null) {
            Result(true)
        } else {
            Result(false, errorMessage)
        }
    }

    fun validateCounter(counter: String): Result {
        val errorMessage = when {
            counter.isEmpty() ||
                    counter.toIntOrNull() == null ||
                    counter.toInt() < COUNTER_MIN_VALUE -> "Counter must be ≥ 0"

            else -> null
        }

        return if (errorMessage == null) {
            Result(true)
        } else {
            Result(false, errorMessage)
        }
    }
}
