package com.ps.tokky.data.repositories

import com.ps.tokky.data.database.TokensDao
import com.ps.tokky.data.models.TokenEntry

class TokensRepository(private val tokensDao: TokensDao) {

    suspend fun getAllTokens(): List<TokenEntry> = tokensDao.getAllTokens()

    suspend fun findTokenWithId(tokenId: String) = tokensDao.findTokenWithId(tokenId)

    suspend fun findTokenWithName(issuer: String, label: String) =
        tokensDao.findTokenWithName(issuer, label)

    suspend fun insertTokens(tokens: List<TokenEntry>) = tokensDao.insertTokens(tokens)

    suspend fun insertToken(token: TokenEntry) = tokensDao.insertToken(token)

    suspend fun upsertToken(token: TokenEntry) = tokensDao.upsertToken(token)

    suspend fun deleteToken(tokenId: String) = tokensDao.deleteToken(tokenId)
}