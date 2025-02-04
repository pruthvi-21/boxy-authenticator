package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.data.models.TokenEntry
import com.boxy.authenticator.domain.repository.TokenRepository

class FetchTokenByIdUseCase(
    private val tokenRepository: TokenRepository,
) {
    operator fun invoke(tokenId: String): Result<TokenEntry> = runCatching {
        tokenRepository.findTokenWithId(tokenId)
    }
}