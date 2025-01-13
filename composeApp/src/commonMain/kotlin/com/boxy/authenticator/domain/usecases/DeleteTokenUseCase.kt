package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.data.repositories.TokensRepository

class DeleteTokenUseCase(
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