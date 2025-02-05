package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.database.repository.TokenRepository

class InsertTokensUseCase(
    private val tokenRepository: TokenRepository,
) {
    operator fun invoke(tokens: List<TokenEntry>) = runCatching {
        tokenRepository.insertTokens(tokens)
    }
}