package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.data.models.TokenEntry
import com.boxy.authenticator.data.repositories.TokensRepository
import com.boxy.authenticator.utils.TokenNameExistsException

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