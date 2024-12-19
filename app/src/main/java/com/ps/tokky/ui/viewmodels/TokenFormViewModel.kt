package com.ps.tokky.ui.viewmodels

import android.content.res.Resources
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ps.tokky.R
import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.utils.AccountEntryMethod
import com.ps.tokky.utils.Constants.DEFAULT_DIGITS
import com.ps.tokky.utils.Constants.DEFAULT_HASH_ALGORITHM
import com.ps.tokky.utils.Constants.DEFAULT_PERIOD
import com.ps.tokky.utils.Constants.DIGITS_MAX_VALUE
import com.ps.tokky.utils.Constants.DIGITS_MIN_VALUE
import com.ps.tokky.utils.Constants.THUMBNAIL_COlORS
import com.ps.tokky.utils.HashAlgorithm
import com.ps.tokky.utils.TokenBuilder
import com.ps.tokky.utils.cleanSecretKey
import com.ps.tokky.utils.isValidSecretKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TokenFormViewModel @Inject constructor(
    private val formValidator: TokenFormValidator
) : ViewModel() {
    private var initialState = TokenFormState()

    private var _uiState = mutableStateOf(initialState)
    val uiState: State<TokenFormState> = _uiState

    val validationEvent = MutableSharedFlow<TokenFormValidationEvent>()

    var isInEditMode: Boolean = false
    var tokenToEdit: TokenEntry? = null

    fun setInitialStateFromToken(token: TokenEntry) {
        val tokenState = TokenFormState(
            issuer = token.issuer,
            label = token.label,
            secretKey = token.secretKey,
            thumbnailColor = token.thumbnailColor,
            algorithm = token.algorithm,
            period = token.period.toString(),
            digits = token.digits.toString()
        )

        initialState = tokenState
        _uiState.value = tokenState
    }

    fun onEvent(event: TokenFormEvent) {
        when (event) {
            is TokenFormEvent.IssuerChanged -> {
                updateState { copy(issuer = event.issuer, validationErrors = validationErrors - "issuer") }
            }

            is TokenFormEvent.LabelChanged -> {
                updateState { copy(label = event.label) }
            }

            is TokenFormEvent.SecretKeyChanged -> {
                updateState {
                    copy(
                        secretKey = event.secretKey.cleanSecretKey(),
                        validationErrors = validationErrors - "secretKey"
                    )
                }
            }

            is TokenFormEvent.ThumbnailColorChanged -> {
                updateState { copy(thumbnailColor = event.thumbnailColor) }
            }

            is TokenFormEvent.AlgorithmChanged -> {
                updateState { copy(algorithm = event.algorithm) }
            }

            is TokenFormEvent.PeriodChanged -> {
                updateState { copy(period = event.period, validationErrors = validationErrors - "period") }
            }

            is TokenFormEvent.DigitsChanged -> {
                updateState { copy(digits = event.digits, validationErrors = validationErrors - "digits") }
            }

            is TokenFormEvent.EnableAdvancedOptionsChanged -> {
                updateState { copy(enableAdvancedOptions = event.enableAdvancedOptions) }
            }

            is TokenFormEvent.Submit -> {
                validateInputs()
            }
        }
    }

    private fun updateState(newState: TokenFormState.() -> TokenFormState) {
        _uiState.value = _uiState.value.newState()
    }

    private fun validateInputs() {
        val issuerResult = formValidator.validateIssuer(_uiState.value.issuer)
        val secretKeyResult = formValidator.validateSecretKey(_uiState.value.secretKey)
        val periodResult = formValidator.validatePeriod(_uiState.value.period)
        val digitsResult = formValidator.validateDigits(_uiState.value.digits)

        _uiState.value = _uiState.value.copy(
            validationErrors = mapOf(
                "issuer" to issuerResult.errorMessage.takeIf { !issuerResult.isValid },
                "secretKey" to secretKeyResult.errorMessage.takeIf { !secretKeyResult.isValid },
                "period" to periodResult.errorMessage.takeIf { !periodResult.isValid },
                "digits" to digitsResult.errorMessage.takeIf { !digitsResult.isValid }
            )
        )
        val hasError = listOf(
            issuerResult,
            secretKeyResult,
        ).any { !it.isValid }

        viewModelScope.launch {
            if (!hasError) {
                val state = _uiState.value

                val token = if (isInEditMode) {
                    tokenToEdit!!.copy(
                        issuer = state.issuer,
                        label = state.label,
                        thumbnailColor = state.thumbnailColor,
                        updatedOn = Date(),
                    )
                } else {
                    var token = TokenBuilder.buildNewToken(
                        issuer = state.issuer,
                        label = state.label,
                        secretKey = state.secretKey,
                        thumbnailColor = state.thumbnailColor,
                        addedFrom = AccountEntryMethod.FORM,
                    )

                    if (state.enableAdvancedOptions) {
                        token = token.copy(
                            algorithm = state.algorithm,
                            period = state.period.toInt(),
                            digits = state.digits.toInt()
                        )
                    }
                    token
                }

                validationEvent.emit(TokenFormValidationEvent.Success(token))
                onValidationSuccess()
            }
        }
    }

    private fun onValidationSuccess() {

    }

    // Reset form
    fun dispose() {
        val resetState = TokenFormState()
        _uiState.value = resetState
        initialState = resetState
    }

    fun isFormUpdated(): Boolean {
        // Ignoring enableAdvancedOptions and validationErrors
        // since they don't represent user-modified data.
        val currentState = _uiState.value
        return currentState.copy(
            enableAdvancedOptions = initialState.enableAdvancedOptions,
            validationErrors = initialState.validationErrors
        ) != initialState
    }
}

sealed class TokenFormValidationEvent {
    data class Success(val token: TokenEntry) : TokenFormValidationEvent()
}

class TokenFormValidator @Inject constructor(
    val resources: Resources
) {

    data class Result(
        val isValid: Boolean = false,
        val errorMessage: String? = null
    )

    fun validateIssuer(issuer: String): Result {
        return if (issuer.isNotEmpty()) {
            Result(true)
        } else {
            Result(false, resources.getString(R.string.error_issuer_empty))
        }
    }

    fun validateSecretKey(secretKey: String): Result {
        return if (secretKey.isEmpty()) {
            Result(false, resources.getString(R.string.error_secret_key_empty))
        } else if (!secretKey.isValidSecretKey()) {
            Result(false, resources.getString(R.string.error_secret_key_invalid))
        } else {
            Result(true)
        }
    }

    fun validatePeriod(period: String): Result {
        val errorMessage = when {
            period.isEmpty() -> resources.getString(R.string.error_period_empty)
            period.toIntOrNull() == null || period.toInt() == 0 -> resources.getString(R.string.error_period_invalid)
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
            digits.isEmpty() -> resources.getString(R.string.error_digits_empty)
            digits.toIntOrNull() == null ||
                    digits.toInt() < DIGITS_MIN_VALUE ||
                    digits.toInt() > DIGITS_MAX_VALUE -> resources.getString(R.string.error_digits_invalid)

            else -> null
        }

        return if (errorMessage == null) {
            Result(true)
        } else {
            Result(false, errorMessage)
        }
    }
}

data class TokenFormState(
    val issuer: String = "",
    val label: String = "",
    val secretKey: String = "",
    val thumbnailColor: Int = THUMBNAIL_COlORS.random(),
    val algorithm: HashAlgorithm = DEFAULT_HASH_ALGORITHM,
    val period: String = "$DEFAULT_PERIOD",
    val digits: String = "$DEFAULT_DIGITS",
    val enableAdvancedOptions: Boolean = false,
    val validationErrors: Map<String, String?> = emptyMap()
)

sealed class TokenFormEvent {
    data class IssuerChanged(val issuer: String) : TokenFormEvent()
    data class LabelChanged(val label: String) : TokenFormEvent()
    data class SecretKeyChanged(val secretKey: String) : TokenFormEvent()
    data class ThumbnailColorChanged(val thumbnailColor: Int) : TokenFormEvent()
    data class AlgorithmChanged(val algorithm: HashAlgorithm) : TokenFormEvent()
    data class PeriodChanged(val period: String) : TokenFormEvent()
    data class DigitsChanged(val digits: String) : TokenFormEvent()
    data class EnableAdvancedOptionsChanged(val enableAdvancedOptions: Boolean) : TokenFormEvent()
    data object Submit : TokenFormEvent()
}