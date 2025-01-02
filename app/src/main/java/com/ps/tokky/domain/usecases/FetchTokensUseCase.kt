package com.ps.tokky.domain.usecases

import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.data.repositories.TokensRepository
import javax.inject.Inject

class FetchTokensUseCase @Inject constructor(
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