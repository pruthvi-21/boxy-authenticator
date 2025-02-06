package com.boxy.authenticator.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxy.authenticator.core.AppSettings
import com.boxy.authenticator.core.Logger
import com.boxy.authenticator.core.TokenEntryParser
import com.boxy.authenticator.core.TokenFormValidator
import com.boxy.authenticator.core.encoding.Base32
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.models.enums.AccountEntryMethod
import com.boxy.authenticator.domain.models.enums.OTPType
import com.boxy.authenticator.domain.models.enums.TokenSetupMode
import com.boxy.authenticator.domain.models.form.TokenFormEvent
import com.boxy.authenticator.domain.models.form.TokenFormState
import com.boxy.authenticator.domain.models.otp.HotpInfo
import com.boxy.authenticator.domain.models.otp.OtpInfo
import com.boxy.authenticator.domain.models.otp.SteamInfo
import com.boxy.authenticator.domain.models.otp.TotpInfo
import com.boxy.authenticator.domain.usecases.DeleteTokenUseCase
import com.boxy.authenticator.domain.usecases.InsertTokenUseCase
import com.boxy.authenticator.domain.usecases.ReplaceExistingTokenUseCase
import com.boxy.authenticator.domain.usecases.UpdateTokenUseCase
import com.boxy.authenticator.utils.TokenNameExistsException
import com.boxy.authenticator.utils.cleanSecretKey
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class TokenSetupViewModel(
    private val settings: AppSettings,
    private val insertTokenUseCase: InsertTokenUseCase,
    private val updateTokenUseCase: UpdateTokenUseCase,
    private val deleteTokenUseCase: DeleteTokenUseCase,
    private val replaceExistingTokenUseCase: ReplaceExistingTokenUseCase,
    private val formValidator: TokenFormValidator,
) : ViewModel() {
    private var initialState = TokenFormState()

    private var _uiState = mutableStateOf(initialState)
    val uiState: State<TokenFormState> = _uiState

    var tokenSetupMode: TokenSetupMode = TokenSetupMode.NEW
    private var tokenToUpdate: TokenEntry? = null

    val lockSensitiveFields: Boolean
        get() = mutableStateOf(settings.isLockSensitiveFieldsEnabled()).value
                && uiState.value.isInEditMode

    val showBackPressDialog = mutableStateOf(false)
    val showDeleteTokenDialog = mutableStateOf(false)
    val showDuplicateTokenDialog = mutableStateOf(DuplicateTokenDialogArgs(false))

    data class DuplicateTokenDialogArgs(
        val show: Boolean,
        val token: TokenEntry? = null,
        val existingToken: TokenEntry? = null,
    )

    fun setInitialStateFromToken(token: TokenEntry) {
        tokenToUpdate = token
        tokenSetupMode = TokenSetupMode.UPDATE
        setStateFromToken(token)
    }

    fun setInitialStateFromUrl(authUrl: String) {
        tokenSetupMode = TokenSetupMode.URL
        setStateFromToken(TokenEntryParser.buildFromUrl(authUrl))
    }

    private fun setStateFromToken(token: TokenEntry) {
        var tokenState = TokenFormState(
            issuer = token.issuer,
            label = token.label,
            thumbnail = token.thumbnail,
            secretKey = Base32.encode(token.otpInfo.secretKey),
            algorithm = token.otpInfo.algorithm,
            digits = token.otpInfo.digits.toString(),
            isInEditMode = true,
        )

        tokenState = when (token.otpInfo) {
            is HotpInfo -> tokenState.copy(
                type = OTPType.HOTP,
                counter = token.otpInfo.counter.toString()
            )

            is SteamInfo -> tokenState.copy(type = OTPType.STEAM)
            is TotpInfo -> tokenState.copy(
                type = OTPType.TOTP,
                period = token.otpInfo.period.toString()
            )
        }
        tokenState = updateFieldVisibilityState(tokenState)

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
                _uiState.value = updateFieldVisibilityState(_uiState.value)
            }

            is TokenFormEvent.ThumbnailChanged -> {
                updateState { copy(thumbnail = event.thumbnail) }
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
                validateInputs(event)
            }
        }
    }

    private fun updateState(newState: TokenFormState.() -> TokenFormState) {
        _uiState.value = _uiState.value.newState()
    }

    private suspend fun handleValidationResult(result: TokenFormValidator.Result): String? {
        return when (result) {
            is TokenFormValidator.Result.Success -> null
            is TokenFormValidator.Result.Failure -> getString(result.errorMessage)
        }
    }

    private fun validateInputs(event: TokenFormEvent.Submit) {
        viewModelScope.launch {
            val issuerResult = formValidator.validateIssuer(_uiState.value.issuer)
            val secretKeyResult = formValidator.validateSecretKey(_uiState.value.secretKey)
            val periodResult = formValidator.validatePeriod(_uiState.value.period)
            val counterResult = formValidator.validateCounter(_uiState.value.counter)

            _uiState.value = _uiState.value.copy(
                validationErrors = mapOf(
                    "issuer" to handleValidationResult(issuerResult),
                    "secretKey" to handleValidationResult(secretKeyResult),
                    "period" to handleValidationResult(periodResult),
                    "counter" to handleValidationResult(counterResult)
                )
            )

            val state = _uiState.value

            fun buildOtpInfo(): OtpInfo {
                return when (state.type) {
                    OTPType.TOTP -> {
                        val totpResults =
                            listOf(issuerResult, secretKeyResult, periodResult)
                        val hasError = totpResults.any { it is TokenFormValidator.Result.Failure }
                        if (hasError) throw Exception()

                        TotpInfo(
                            Base32.decode(uiState.value.secretKey),
                            state.algorithm,
                            state.digits.toInt(),
                            state.period.toLong(),
                        )
                    }

                    OTPType.HOTP -> {
                        val hotpResults =
                            listOf(issuerResult, secretKeyResult, counterResult)
                        val hasError = hotpResults.any { it is TokenFormValidator.Result.Failure }
                        if (hasError) throw Exception()

                        HotpInfo(
                            Base32.decode(uiState.value.secretKey),
                            state.algorithm,
                            state.digits.toInt(),
                            state.counter.toLong(),
                        )
                    }

                    OTPType.STEAM -> {
                        val steamResults = listOf(issuerResult, secretKeyResult)
                        val hasError = steamResults.any { it is TokenFormValidator.Result.Failure }
                        if (hasError) throw Exception()

                        SteamInfo(Base32.decode(uiState.value.secretKey))
                    }
                }
            }

            try {
                val otpInfo = buildOtpInfo()

                when (tokenSetupMode) {
                    TokenSetupMode.NEW,
                    TokenSetupMode.URL,
                        -> {
                        var newToken = TokenEntry.create(
                            issuer = state.issuer,
                            label = state.label,
                            thumbnail = state.thumbnail,
                            otpInfo = otpInfo,
                            addedFrom = AccountEntryMethod.FORM,
                        )

                        if (tokenSetupMode == TokenSetupMode.URL) {
                            newToken = newToken.copy(addedFrom = AccountEntryMethod.QR_CODE)
                        }

                        insertToken(newToken, event)
                    }

                    TokenSetupMode.UPDATE -> {
                        val token = tokenToUpdate?.copy(
                            issuer = state.issuer,
                            label = state.label,
                            thumbnail = state.thumbnail,
                            otpInfo = otpInfo,
                        )
                            ?: throw IllegalStateException("No token ID available for update")

                        updateToken(token, event)
                    }
                }
            } catch (e: Exception) {
                Logger.e(TAG, "validateInputs: Exception while validating", e)
            }
        }
    }

    private fun insertToken(
        token: TokenEntry,
        event: TokenFormEvent.Submit,
    ) {
        insertTokenUseCase(token)
            .onSuccess { event.onComplete() }
            .onFailure { exception ->
                Logger.e(TAG, "insertToken: Failed to insert token", exception)

                if (exception is TokenNameExistsException) {
                    exception.token?.let { event.onDuplicate(token, it) }
                } else {
                    // TODO: display a error
                }
            }
    }

    private fun updateToken(
        token: TokenEntry,
        event: TokenFormEvent.Submit,
    ) {
        updateTokenUseCase(token)
            .onSuccess { event.onComplete() }
            .onFailure {
                Logger.e(TAG, "updateToken: Failed to update token", it)
                // TODO: display a error
            }
    }

    private fun updateFieldVisibilityState(state: TokenFormState): TokenFormState {
        return when (state.type) {
            OTPType.TOTP -> state.copy(
                isAlgorithmFieldVisible = true,
                isDigitsFieldVisible = true,
                isPeriodFieldVisible = true,
                isCounterFieldVisible = false,
            )

            OTPType.HOTP -> state.copy(
                isAlgorithmFieldVisible = true,
                isDigitsFieldVisible = true,
                isPeriodFieldVisible = false,
                isCounterFieldVisible = true,
            )

            OTPType.STEAM -> state.copy(
                isAlgorithmFieldVisible = false,
                isDigitsFieldVisible = false,
                isPeriodFieldVisible = false,
                isCounterFieldVisible = false,
            )
        }
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
            validationErrors = initialState.validationErrors,
        ) != initialState
    }

    fun deleteToken(tokenId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            deleteTokenUseCase(tokenId)
            onComplete()
        }
    }

    fun replaceExistingToken(existingToken: TokenEntry, token: TokenEntry) {
        viewModelScope.launch {
            replaceExistingTokenUseCase(existingToken, token)
        }
    }

    companion object {
        private const val TAG = "TokenSetupViewModel"
    }
}