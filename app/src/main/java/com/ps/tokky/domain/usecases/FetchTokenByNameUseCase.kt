package com.ps.tokky.domain.usecases

import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.data.repositories.TokensRepository

class FetchTokenByNameUseCase(
    private val tokensRepository: TokensRepository,
) {
    suspend operator fun invoke(issuer: String, label: String): Result<TokenEntry?> {
        return try {
            val token = tokensRepository.findTokenWithName(issuer, label)
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}