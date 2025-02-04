package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.data.models.TokenEntry
import com.boxy.authenticator.domain.repository.TokenRepository

class ReplaceExistingTokenUseCase(
    private val tokenRepository: TokenRepository,
) {
    operator fun invoke(existingToken: TokenEntry, token: TokenEntry) = runCatching {
        tokenRepository.replaceTokenWith(existingToken.id, token)
    }
}
