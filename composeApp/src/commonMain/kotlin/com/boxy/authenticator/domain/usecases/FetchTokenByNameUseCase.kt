package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.database.repository.TokenRepository

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