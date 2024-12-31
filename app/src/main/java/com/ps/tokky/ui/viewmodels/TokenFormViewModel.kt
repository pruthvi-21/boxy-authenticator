package com.ps.tokky.ui.viewmodels

import android.content.res.Resources
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ps.tokky.R
import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.data.models.otp.HotpInfo
import com.ps.tokky.data.models.otp.HotpInfo.Companion.COUNTER_MIN_VALUE
import com.ps.tokky.data.models.otp.HotpInfo.Companion.DEFAULT_COUNTER
import com.ps.tokky.data.models.otp.OtpInfo
import com.ps.tokky.data.models.otp.OtpInfo.Companion.DEFAULT_ALGORITHM
import com.ps.tokky.data.models.otp.OtpInfo.Companion.DEFAULT_DIGITS
import com.ps.tokky.data.models.otp.TotpInfo
import com.ps.tokky.data.models.otp.TotpInfo.Companion.DEFAULT_PERIOD
import com.ps.tokky.data.repositories.TokensRepository
import com.ps.tokky.utils.AccountEntryMethod
import com.ps.tokky.utils.Base32
import com.ps.tokky.utils.Constants.DIGITS_MAX_VALUE
import com.ps.tokky.utils.Constants.DIGITS_MIN_VALUE
import com.ps.tokky.utils.Constants.THUMBNAIL_COlORS
import com.ps.tokky.utils.OTPType
import com.ps.tokky.utils.cleanSecretKey
import com.ps.tokky.utils.isValidSecretKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

private const val TAG = "TokenFormViewModel"

enum class TokenSetupMode {
    NEW, URL, UPDATE
}

@HiltViewModel
class TokenFormViewModel @Inject constructor(
    private val tokensRepository: TokensRepository,
    private val formValidator: TokenFormValidator,
) : ViewModel() {
    private var initialState = TokenFormState()

    private var _uiState = mutableStateOf(initialState)
    val uiState: State<TokenFormState> = _uiState

    val validationEvent = MutableSharedFlow<TokenFormValidationEvent>()

    var tokenSetupMode: TokenSetupMode = TokenSetupMode.NEW
    private var tokenToUpdate: TokenEntry? = null

    val showBackPressDialog = mutableStateOf(false)
    val showDeleteTokenDialog = mutableStateOf(false)
    val showDuplicateTokenDialog = mutableStateOf(DuplicateTokenDialogArgs(false))

    data class DuplicateTokenDialogArgs(
        val show: Boolean,
        val token: TokenEntry? = null,
        val existingToken: TokenEntry? = null,
    )

    fun setInitialStateFromTokenWithId(tokenId: String) {
        viewModelScope.launch {
            val token = tokensRepository.getTokenWithId(tokenId)
            tokenToUpdate = token
            tokenSetupMode = TokenSetupMode.UPDATE
            setInitialStateFromToken(token)
        }
    }

    fun setInitialStateFromUrl(authUrl: String) {
        tokenSetupMode = TokenSetupMode.URL
        setInitialStateFromToken(TokenEntry.buildFromUrl(authUrl))
    }

    private fun setInitialStateFromToken(token: TokenEntry) {
        var tokenState = TokenFormState(
            issuer = token.issuer,
            label = token.label,
            thumbnailColor = token.thumbnailColor,
            type = token.type,
            secretKey = Base32.encode(token.otpInfo.secretKey),
            algorithm = token.otpInfo.algorithm,
            digits = token.otpInfo.digits.toString()
        )

        tokenState = when (token.type) {
            OTPType.TOTP -> tokenState.copy(period = (token.otpInfo as TotpInfo).period.toString())

            OTPType.HOTP -> tokenState.copy(counter = (token.otpInfo as HotpInfo).counter.toString())
        }

        initialState = tokenState
        _uiState.value = tokenState
    }

    fun onEvent(event: TokenFormEvent) {
        when (event) {
            is TokenFormEvent.IssuerChanged -> {
                updateState {
                    copy(
                        issuer = event.issuer,
                        validationErrors = validationErrors - "issuer"
                    )
                }
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

            is TokenFormEvent.TypeChanged -> {
                updateState { copy(type = event.type) }
            }

            is TokenFormEvent.ThumbnailColorChanged -> {
                updateState { copy(thumbnailColor = event.thumbnailColor) }
            }

            is TokenFormEvent.AlgorithmChanged -> {
                updateState { copy(algorithm = event.algorithm) }
            }

            is TokenFormEvent.PeriodChanged -> {
                updateState {
                    copy(
                        period = event.period,
                        validationErrors = validationErrors - "period"
                    )
                }
            }

            is TokenFormEvent.DigitsChanged -> {
                updateState {
                    copy(
                        digits = event.digits,
                        validationErrors = validationErrors - "digits"
                    )
                }
            }

            is TokenFormEvent.CounterChanged -> {
                updateState {
                    copy(
                        counter = event.counter,
                        validationErrors = validationErrors - "counter"
                    )
                }
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
        val counterResult = formValidator.validateCounter(_uiState.value.counter)

        _uiState.value = _uiState.value.copy(
            validationErrors = mapOf(
                "issuer" to issuerResult.errorMessage.takeIf { !issuerResult.isValid },
                "secretKey" to secretKeyResult.errorMessage.takeIf { !secretKeyResult.isValid },
                "period" to periodResult.errorMessage.takeIf { !periodResult.isValid },
                "digits" to digitsResult.errorMessage.takeIf { !digitsResult.isValid },
                "counter" to counterResult.errorMessage.takeIf { !counterResult.isValid }
            )
        )

        val state = _uiState.value

        fun buildOtpInfo(): OtpInfo {
            return when (state.type) {
                OTPType.TOTP -> {
                    val totpResults =
                        listOf(issuerResult, secretKeyResult, digitsResult, periodResult)
                    val hasError = totpResults.any { !it.isValid }
                    if (hasError) throw Exception()

                    TotpInfo(
                        Base32.decode(uiState.value.secretKey),
                        state.algorithm,
                        state.digits.toInt(),
                        state.period.toInt(),
                    )
                }

                OTPType.HOTP -> {
                    val hotpResults =
                        listOf(issuerResult, secretKeyResult, digitsResult, counterResult)
                    val hasError = hotpResults.any { !it.isValid }
                    if (hasError) throw Exception()

                    HotpInfo(
                        Base32.decode(uiState.value.secretKey),
                        state.algorithm,
                        state.digits.toInt(),
                        state.counter.toLong(),
                    )
                }
            }
        }

        viewModelScope.launch {
            try {
                val otpInfo = buildOtpInfo()

                val token = when (tokenSetupMode) {
                    TokenSetupMode.NEW,
                    TokenSetupMode.URL,
                        -> {
                        var newToken = TokenEntry.buildNewToken(
                            issuer = state.issuer,
                            label = state.label,
                            type = state.type,
                            thumbnailColor = state.thumbnailColor,
                            otpInfo = otpInfo,
                            addedFrom = AccountEntryMethod.FORM,
                        )

                        if (tokenSetupMode == TokenSetupMode.URL) {
                            newToken = newToken.copy(addedFrom = AccountEntryMethod.QR_CODE)
                        }

                        newToken
                    }

                    TokenSetupMode.UPDATE -> {
                        tokenToUpdate?.copy(
                            issuer = state.issuer,
                            label = state.label,
                            thumbnailColor = state.thumbnailColor,
                            updatedOn = Date()
                        ) ?: throw IllegalStateException("No token ID available for update")
                    }
                }

                validationEvent.emit(TokenFormValidationEvent.Success(token))
                onValidationSuccess()
            } catch (e: Exception) {
                Log.i(TAG, "validateInputs: ", e)
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
    val resources: Resources,
) {

    data class Result(
        val isValid: Boolean = false,
        val errorMessage: String? = null,
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

    fun validateCounter(counter: String): Result {
        val errorMessage = when {
            counter.isEmpty() ||
                    counter.toIntOrNull() == null ||
                    counter.toInt() < COUNTER_MIN_VALUE -> resources.getString(R.string.error_counter_invalid)

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
    val type: OTPType = OTPType.TOTP,
    val thumbnailColor: Int = THUMBNAIL_COlORS.random(),
    val algorithm: String = DEFAULT_ALGORITHM,
    val period: String = "$DEFAULT_PERIOD",
    val digits: String = "$DEFAULT_DIGITS",
    val counter: String = "$DEFAULT_COUNTER",
    val enableAdvancedOptions: Boolean = false,
    val validationErrors: Map<String, String?> = emptyMap(),
)

sealed class TokenFormEvent {
    data class IssuerChanged(val issuer: String) : TokenFormEvent()
    data class LabelChanged(val label: String) : TokenFormEvent()
    data class SecretKeyChanged(val secretKey: String) : TokenFormEvent()
    data class TypeChanged(val type: OTPType) : TokenFormEvent()
    data class ThumbnailColorChanged(val thumbnailColor: Int) : TokenFormEvent()
    data class AlgorithmChanged(val algorithm: String) : TokenFormEvent()
    data class PeriodChanged(val period: String) : TokenFormEvent()
    data class DigitsChanged(val digits: String) : TokenFormEvent()
    data class CounterChanged(val counter: String) : TokenFormEvent()
    data class EnableAdvancedOptionsChanged(val enableAdvancedOptions: Boolean) : TokenFormEvent()
    data object Submit : TokenFormEvent()
}