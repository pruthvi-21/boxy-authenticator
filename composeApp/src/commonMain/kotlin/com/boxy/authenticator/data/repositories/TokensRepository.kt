package com.boxy.authenticator.data.repositories

import com.boxy.authenticator.data.database.TokensDao
import com.boxy.authenticator.data.models.TokenEntry

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