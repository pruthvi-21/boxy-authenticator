package com.ps.tokky.domain.usecases

import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.data.repositories.TokensRepository
import com.ps.tokky.utils.TokenNameExistsException

class InsertTokenUseCase(
    private val tokensRepository: TokensRepository
) {
    suspend operator fun invoke(token: TokenEntry, replaceIfExists: Boolean = false): Result<Unit> {
        return try {
            val existingToken = tokensRepository.findTokenWithName(token.issuer, token.label)
            if (existingToken != null && existingToken.id != token.id) {
                return Result.failure(
                    TokenNameExistsException(existingToken, "Token already exists.")
                )
            }
            if (replaceIfExists) {
                tokensRepository.upsertToken(token)
            } else {
                tokensRepository.insertToken(token)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}