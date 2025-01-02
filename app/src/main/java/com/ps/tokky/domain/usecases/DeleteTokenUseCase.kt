package com.ps.tokky.domain.usecases

import com.ps.tokky.data.repositories.TokensRepository
import javax.inject.Inject

class DeleteTokenUseCase @Inject constructor(
    private val tokensRepository: TokensRepository,
) {
    suspend operator fun invoke(tokenId: String): Result<Unit> {
        return try {
            tokensRepository.deleteToken(tokenId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}