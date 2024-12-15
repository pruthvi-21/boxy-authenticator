package com.ps.tokky.data.repositories

import com.ps.tokky.data.database.TokensDao
import com.ps.tokky.data.models.TokenEntry

class TokensRepository(private val tokensDao: TokensDao) {

    suspend fun getAllTokens(): List<TokenEntry> = tokensDao.getAllTokens()

    suspend fun insertAccounts(accounts: List<TokenEntry>) = tokensDao.insertAccounts(accounts)

    suspend fun insertToken(token: TokenEntry) = tokensDao.insertToken(token)

    suspend fun upsertToken(token: TokenEntry) = tokensDao.upsertToken(token)

    suspend fun deleteToken(tokenId: String) = tokensDao.deleteToken(tokenId)

    suspend fun findDuplicateToken(issuer: String, label: String, ignoreId: String?) =
        tokensDao.findDuplicateToken(issuer, label, ignoreId)
}