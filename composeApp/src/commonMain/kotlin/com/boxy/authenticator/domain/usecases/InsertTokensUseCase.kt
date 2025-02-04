package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.data.models.TokenEntry
import com.boxy.authenticator.domain.repository.TokenRepository

class InsertTokensUseCase(
    private val tokenRepository: TokenRepository,
) {
    operator fun invoke(tokens: List<TokenEntry>) = runCatching {
        tokenRepository.insertTokens(tokens)
    }
}