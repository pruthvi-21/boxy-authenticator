package com.boxy.authenticator.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxy.authenticator.core.SettingsDataStore
import com.boxy.authenticator.core.Logger
import com.boxy.authenticator.core.TokenEntryParser
import com.boxy.authenticator.core.TokenFormValidator
import com.boxy.authenticator.core.encoding.Base32
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.models.enums.AccountEntryMethod
import com.boxy.authenticator.domain.models.enums.OTPType
import com.boxy.authenticator.domain.models.enums.TokenSetupMode
import com.boxy.authenticator.domain.models.form.TokenFormEvent
import com.boxy.authenticator.domain.models.otp.HotpInfo
import com.boxy.authenticator.domain.models.otp.OtpInfo
import com.boxy.authenticator.domain.models.otp.SteamInfo
import com.boxy.authenticator.domain.models.otp.TotpInfo
import com.boxy.authenticator.domain.usecases.DeleteTokenUseCase
import com.boxy.authenticator.domain.usecases.FetchTokenByIdUseCase
import com.boxy.authenticator.domain.usecases.InsertTokenUseCase
import com.boxy.authenticator.domain.usecases.ReplaceExistingTokenUseCase
import com.boxy.authenticator.domain.usecases.UpdateTokenUseCase
import com.boxy.authenticator.ui.state.TokenSetupUiState
import com.boxy.authenticator.utils.TokenNameExistsException
import com.boxy.authenticator.utils.cleanSecretKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class TokenSetupViewModel(
    private val settings: SettingsDataStore,
    private val fetchTokenByIdUseCase: FetchTokenByIdUseCase,
    private val insertTokenUseCase: InsertTokenUseCase,
    private val updateTokenUseCase: UpdateTokenUseCase,
    private val deleteTokenUseCase: DeleteTokenUseCase,
    private val replaceExistingTokenUseCase: ReplaceExistingTokenUseCase,
    private val formValidator: TokenFormValidator,
) : ViewModel() {
    private val logger = Logger("TokenSetupViewModel")

    private var currentToken: TokenEntry? = null

    private var initialUiState = TokenSetupUiState() // only required to check if form is updated
    private val _uiState = MutableStateFlow(initialUiState)
    val uiState = _uiState.asStateFlow()

    val lockSensitiveFields: Boolean
        get() = mutableStateOf(settings.isLockSensitiveFieldsEnabled()).value
                && uiState.value.isInEditMode

    data class DuplicateTokenDialogArgs(
        val show: Boolean,
        val token: TokenEntry? = null,
        val existingToken: TokenEntry? = null,
    )

    fun setStateFromToken(token: TokenEntry?, setupMode: TokenSetupMode) {
        if (token == null) return

        currentToken = token
        _uiState.value = _uiState.value.copy(
            issuer = token.issuer,
            label = token.label,
            thumbnail = token.thumbnail,
            secretKey = Base32.encode(token.otpInfo.secretKey),
            algorithm = token.otpInfo.algorithm,
            digits = token.otpInfo.digits.toString(),
            isInEditMode = true,
            tokenSetupMode = setupMode
        )

        _uiState.value = when (token.otpInfo) {
            is HotpInfo -> _uiState.value.copy(
                type = OTPType.HOTP,
                counter = token.otpInfo.counter.toString()
            )

            is SteamInfo -> _uiState.value.copy(type = OTPType.STEAM)
            is TotpInfo -> _uiState.value.copy(
                type = OTPType.TOTP,
                period = token.otpInfo.period.toString()
            )
        }
        updateFieldVisibilityState()

        initialUiState = _uiState.value
    }

    fun setStateFromAuthUrl(authUrl: String) {
        val token = try {
            TokenEntryParser.buildFromUrl(authUrl)
        } catch (e: Exception) {
            null
        }

        setStateFromToken(token, TokenSetupMode.URL)
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
                updateFieldVisibilityState()
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

    private fun updateState(newState: TokenSetupUiState.() -> TokenSetupUiState) {
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

                when (_uiState.value.tokenSetupMode) {
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

                        if (_uiState.value.tokenSetupMode == TokenSetupMode.URL) {
                            newToken = newToken.copy(addedFrom = AccountEntryMethod.QR_CODE)
                        }

                        insertToken(newToken, event)
                    }

                    TokenSetupMode.UPDATE -> {
                        val token = currentToken?.copy(
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
                logger.e("validateInputs: Exception while validating", e)
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
                logger.e("insertToken: Failed to insert token", exception)

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
                logger.e("updateToken: Failed to update token", it)
                // TODO: display a error
            }
    }

    private fun updateFieldVisibilityState() {
        _uiState.value = when (_uiState.value.type) {
            OTPType.TOTP -> _uiState.value.copy(
                isAlgorithmFieldVisible = true,
                isDigitsFieldVisible = true,
                isPeriodFieldVisible = true,
                isCounterFieldVisible = false,
            )

            OTPType.HOTP -> _uiState.value.copy(
                isAlgorithmFieldVisible = true,
                isDigitsFieldVisible = true,
                isPeriodFieldVisible = false,
                isCounterFieldVisible = true,
            )

            OTPType.STEAM -> _uiState.value.copy(
                isAlgorithmFieldVisible = false,
                isDigitsFieldVisible = false,
                isPeriodFieldVisible = false,
                isCounterFieldVisible = false,
            )
        }
    }

    fun isFormUpdated(): Boolean {
        return initialUiState != _uiState.value.copy(
            // ignore these fields
            enableAdvancedOptions = initialUiState.enableAdvancedOptions,
            tokenSetupMode = initialUiState.tokenSetupMode,
            showBackPressDialog = initialUiState.showBackPressDialog,
            showDeleteTokenDialog = initialUiState.showDeleteTokenDialog,
            showDuplicateTokenDialog = initialUiState.showDuplicateTokenDialog,
        )
    }

    fun deleteToken() {
        currentToken?.let { deleteTokenUseCase(it.id) }
    }

    fun replaceExistingToken(existingToken: TokenEntry, token: TokenEntry) {
        replaceExistingTokenUseCase(existingToken, token)
    }

    fun getTokenFromId(tokenId: String): TokenEntry? {
        return fetchTokenByIdUseCase.invoke(tokenId).fold(onSuccess = { it }, onFailure = { null })
    }

    fun showBackPressDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showBackPressDialog = show)
    }

    fun showDeleteTokenDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showDeleteTokenDialog = show)
    }

    fun showDuplicateTokenDialog(args: DuplicateTokenDialogArgs) {
        _uiState.value = _uiState.value.copy(showDuplicateTokenDialog = args)
    }
}