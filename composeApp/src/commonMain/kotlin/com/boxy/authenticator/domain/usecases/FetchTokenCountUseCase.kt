package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.domain.database.repository.TokenRepository

class FetchTokenCountUseCase(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(): Result<Long> = runCatching {
        tokenRepository.getTokensCount()
    }
}