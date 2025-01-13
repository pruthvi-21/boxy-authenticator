package com.boxy.authenticator.domain.models

import com.boxy.authenticator.data.models.TokenEntry
import com.boxy.authenticator.utils.OTPType

sealed class TokenFormEvent {
    data class IssuerChanged(val issuer: String) : TokenFormEvent()
    data class LabelChanged(val label: String) : TokenFormEvent()
    data class SecretKeyChanged(val secretKey: String) : TokenFormEvent()
    data class TypeChanged(val type: OTPType) : TokenFormEvent()
    data class ThumbnailColorChanged(val thumbnailColor: String) : TokenFormEvent()
    data class AlgorithmChanged(val algorithm: String) : TokenFormEvent()
    data class PeriodChanged(val period: String) : TokenFormEvent()
    data class DigitsChanged(val digits: String) : TokenFormEvent()
    data class CounterChanged(val counter: String) : TokenFormEvent()
    data class EnableAdvancedOptionsChanged(val enableAdvancedOptions: Boolean) : TokenFormEvent()
    data class Submit(
        val onComplete: () -> Unit,
        val onDuplicate: (TokenEntry, TokenEntry) -> Unit,
    ) : TokenFormEvent()
}