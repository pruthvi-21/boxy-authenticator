package com.ps.tokky.domain.usecases

import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.data.repositories.TokensRepository

class InsertTokensUseCase(
    private val tokensRepository: TokensRepository,
) {
    suspend operator fun invoke(tokens: List<TokenEntry>): Result<Unit> {
        return try {
            tokensRepository.insertTokens(tokens)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}