package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.data.models.TokenEntry
import com.boxy.authenticator.domain.repository.TokenRepository
import com.boxy.authenticator.utils.TokenNameExistsException

class InsertTokenUseCase(
    private val tokenRepository: TokenRepository,
) {
    operator fun invoke(token: TokenEntry): Result<Unit> = runCatching {
        val existingToken = tokenRepository.findTokenWithName(token.issuer, token.label)

        if (existingToken != null && existingToken.id != token.id) {
            throw TokenNameExistsException(existingToken, "Token already exists.")
        }

        tokenRepository.insertToken(token)
    }
}