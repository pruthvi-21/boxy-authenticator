package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.domain.database.repository.TokenRepository

class DeleteTokenUseCase(
    private val tokenRepository: TokenRepository,
) {
    operator fun invoke(tokenId: String) = runCatching {
        tokenRepository.deleteToken(tokenId)
    }
}