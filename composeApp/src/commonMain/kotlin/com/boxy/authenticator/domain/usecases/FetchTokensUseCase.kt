package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.data.models.TokenEntry
import com.boxy.authenticator.domain.repository.TokenRepository

class FetchTokensUseCase(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(): Result<List<TokenEntry>> = runCatching {
        tokenRepository.getAllTokens()
    }
}