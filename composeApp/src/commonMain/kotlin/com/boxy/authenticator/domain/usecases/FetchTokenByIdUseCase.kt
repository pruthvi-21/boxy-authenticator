package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.database.repository.TokenRepository

class FetchTokenByIdUseCase(
    private val tokenRepository: TokenRepository,
) {
    operator fun invoke(tokenId: String): Result<TokenEntry> = runCatching {
        tokenRepository.findTokenWithId(tokenId)
    }
}