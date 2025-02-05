package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.domain.database.repository.TokenRepository

class UpdateHotpCounterUseCase(
    private val tokenRepository: TokenRepository,
) {
    operator fun invoke(tokenId: String, counter: Long) = runCatching {
            tokenRepository.updateHotpCounter(tokenId, counter)
        }
}