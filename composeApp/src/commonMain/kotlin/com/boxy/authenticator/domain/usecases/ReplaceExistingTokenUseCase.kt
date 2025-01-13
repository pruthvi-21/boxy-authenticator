package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.data.models.TokenEntry
import com.boxy.authenticator.data.repositories.TokensRepository

class ReplaceExistingTokenUseCase(
    private val tokensRepository: TokensRepository,
) {
    suspend operator fun invoke(existingToken: TokenEntry, token: TokenEntry): Result<Unit> {
        return try {
            tokensRepository.deleteToken(existingToken.id)
            tokensRepository.insertToken(token)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}