package com.boxy.authenticator.helpers

import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.error_counter_invalid
import boxy_authenticator.composeapp.generated.resources.error_issuer_empty
import boxy_authenticator.composeapp.generated.resources.error_period_empty
import boxy_authenticator.composeapp.generated.resources.error_period_invalid
import boxy_authenticator.composeapp.generated.resources.error_secret_key_empty
import boxy_authenticator.composeapp.generated.resources.error_secret_key_invalid
import com.boxy.authenticator.data.models.otp.HotpInfo.Companion.COUNTER_MIN_VALUE
import com.boxy.authenticator.utils.Base32
import org.jetbrains.compose.resources.StringResource

class TokenFormValidator {

    sealed class Result {
        data object Success : Result()
        data class Failure(val errorMessage: StringResource) : Result()
    }

    fun validateIssuer(issuer: String): Result {
        return if (issuer.isNotEmpty()) Result.Success
        else Result.Failure(Res.string.error_issuer_empty)
    }

    fun validateSecretKey(secretKey: String): Result {
        return try {
            val decoded = Base32.decode(secretKey)
            if (decoded.isNotEmpty()) Result.Success
            else Result.Failure(Res.string.error_secret_key_empty)
        } catch (e: Exception) {
            Result.Failure(Res.string.error_secret_key_invalid)
        }
    }

    fun validatePeriod(period: String): Result {
        return when {
            period.isEmpty() -> Result.Failure(Res.string.error_period_empty)
            period.toIntOrNull() == null || period.toInt() == 0 -> Result.Failure(Res.string.error_period_invalid)
            else -> Result.Success
        }
    }

    fun validateCounter(counter: String): Result {
        return when {
            counter.isEmpty() ||
                    counter.toIntOrNull() == null ||
                    counter.toInt() < COUNTER_MIN_VALUE -> Result.Failure(Res.string.error_counter_invalid)

            else -> Result.Success
        }
    }
}
