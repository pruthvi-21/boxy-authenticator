package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.domain.database.repository.TokenRepository
import com.boxy.authenticator.domain.models.TokenEntry

class UpdateTokenUseCase(
    private val tokenRepository: TokenRepository,
) {
    operator fun invoke(token: TokenEntry) = runCatching {
        tokenRepository.updateToken(token)
    }
}