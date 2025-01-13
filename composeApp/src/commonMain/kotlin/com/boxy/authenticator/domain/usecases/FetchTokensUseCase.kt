package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.data.models.TokenEntry
import com.boxy.authenticator.data.repositories.TokensRepository

class FetchTokensUseCase(
    private val tokensRepository: TokensRepository
) {
    suspend operator fun invoke(): Result<List<TokenEntry>> {
        return try {
            val tokens = tokensRepository.getAllTokens()
            Result.success(tokens)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}