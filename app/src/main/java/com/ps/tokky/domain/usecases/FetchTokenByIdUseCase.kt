package com.ps.tokky.domain.usecases

import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.data.repositories.TokensRepository
import javax.inject.Inject

class FetchTokenByIdUseCase @Inject constructor(
    private val tokensRepository: TokensRepository,
) {
    suspend operator fun invoke(tokenId: String): Result<TokenEntry?> {
        return try {
            val token = tokensRepository.findTokenWithId(tokenId)
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}