package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.database.repository.TokenRepository

class FetchTokensUseCase(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(): Result<List<TokenEntry>> = runCatching {
        tokenRepository.getAllTokens()
    }
}