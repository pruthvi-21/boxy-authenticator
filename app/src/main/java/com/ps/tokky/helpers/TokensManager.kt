package com.ps.tokky.helpers

import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.data.repositories.TokensRepository
import com.ps.tokky.utils.TokenNameExistsException
import javax.inject.Inject

class TokensManager @Inject constructor(
    private val tokensRepository: TokensRepository,
) {

    sealed class Result<out T> {
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val exception: Throwable) : Result<Nothing>()
    }

    suspend fun fetchTokens(): Result<List<TokenEntry>> {
        return try {
            val tokens = tokensRepository.getAllTokens()
            Result.Success(tokens)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun fetchTokenById(tokenId: String): Result<TokenEntry> {
        return try {
            val token = tokensRepository.findTokenWithId(tokenId)
            Result.Success(token)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun fetchTokenByName(issuer: String, label: String): Result<TokenEntry?> {
        return try {
            val token = tokensRepository.findTokenWithName(issuer, label)
            Result.Success(token)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun insertToken(token: TokenEntry, replaceIfExists: Boolean = false): Result<Unit> {
        return try {
            val existingToken = tokensRepository.findTokenWithName(token.issuer, token.label)
            if (existingToken != null && existingToken.id != token.id) {
                return Result.Error(
                    TokenNameExistsException(existingToken, "Token already exists.")
                )
            }
            handleInsertOrUpdate(token, replaceIfExists)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun insertTokens(tokens: List<TokenEntry>): Result<Unit> {
        return try {
            tokensRepository.insertTokens(tokens)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun replaceExistingToken(existingToken: TokenEntry, token: TokenEntry): Result<Unit> {
        return try {
            tokensRepository.deleteToken(existingToken.id)
            tokensRepository.insertToken(token)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteToken(tokenId: String): Result<Unit> {
        return try {
            tokensRepository.deleteToken(tokenId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun handleInsertOrUpdate(token: TokenEntry, replaceIfExists: Boolean) {
        if (replaceIfExists) {
            tokensRepository.upsertToken(token)
        } else {
            tokensRepository.insertToken(token)
        }
    }
}