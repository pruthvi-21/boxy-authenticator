package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.data.models.TokenEntry
import com.boxy.authenticator.data.repositories.TokensRepository

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