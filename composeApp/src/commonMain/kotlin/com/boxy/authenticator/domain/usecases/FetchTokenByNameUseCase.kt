package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.data.models.TokenEntry
import com.boxy.authenticator.domain.repository.TokenRepository

class FetchTokenByNameUseCase(
    private val tokenRepository: TokenRepository,
) {
    operator fun invoke(
        issuer: String,
        label: String,
    ): Result<TokenEntry?> = runCatching {
        tokenRepository.findTokenWithName(issuer, label)
    }
}