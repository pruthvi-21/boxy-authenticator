package com.ps.tokky.domain.usecases

import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.data.repositories.TokensRepository

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