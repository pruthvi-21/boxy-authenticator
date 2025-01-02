package com.ps.tokky.helpers

import android.content.Context
import androidx.annotation.StringRes
import com.ps.tokky.R
import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.data.models.otp.HotpInfo.Companion.COUNTER_MIN_VALUE
import com.ps.tokky.utils.Constants.DIGITS_MAX_VALUE
import com.ps.tokky.utils.Constants.DIGITS_MIN_VALUE
import com.ps.tokky.utils.isValidSecretKey
import javax.inject.Inject

class TokenFormValidator @Inject constructor(
    context: Context,
) {

    sealed class TokenFormValidationEvent {
        data class Success(val token: TokenEntry) : TokenFormValidationEvent()
    }

    data class Result(
        val isValid: Boolean = false,
        val errorMessage: String? = null,
    )

    fun validateIssuer(issuer: String): Result {
        return if (issuer.isNotEmpty()) {
            Result(true)
        } else {
            Result(false, getString(R.string.error_issuer_empty))
        }
    }

    fun validateSecretKey(secretKey: String): Result {
        return if (secretKey.isEmpty()) {
            Result(false, getString(R.string.error_secret_key_empty))
        } else if (!secretKey.isValidSecretKey()) {
            Result(false, getString(R.string.error_secret_key_invalid))
        } else {
            Result(true)
        }
    }

    fun validatePeriod(period: String): Result {
        val errorMessage = when {
            period.isEmpty() -> getString(R.string.error_period_empty)
            period.toIntOrNull() == null || period.toInt() == 0 -> getString(R.string.error_period_invalid)
            else -> null
        }

        return if (errorMessage == null) {
            Result(true)
        } else {
            Result(false, errorMessage)
        }
    }

    fun validateDigits(digits: String): Result {
        val errorMessage = when {
            digits.isEmpty() -> getString(R.string.error_digits_empty)
            digits.toIntOrNull() == null ||
                    digits.toInt() < DIGITS_MIN_VALUE ||
                    digits.toInt() > DIGITS_MAX_VALUE -> getString(R.string.error_digits_invalid)

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
                    counter.toInt() < COUNTER_MIN_VALUE -> getString(R.string.error_counter_invalid)

            else -> null
        }

        return if (errorMessage == null) {
            Result(true)
        } else {
            Result(false, errorMessage)
        }
    }

    private val resources = context.resources
    private fun getString(@StringRes id: Int) = resources.getString(id)
}
