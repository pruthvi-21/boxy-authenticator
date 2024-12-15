package com.ps.tokky.repositories

import com.ps.tokky.database.TokensDao
import com.ps.tokky.models.TokenEntry
import java.util.ArrayList

class TokensRepository(private val tokensDao: TokensDao) {

    suspend fun getAllTokens(): List<TokenEntry> = tokensDao.getAllTokens()

    suspend fun insertAccounts(accounts: List<TokenEntry>) = tokensDao.insertAccounts(accounts)

    suspend fun insertToken(token: TokenEntry) = tokensDao.insertToken(token)

    suspend fun upsertToken(token: TokenEntry) = tokensDao.upsertToken(token)

    suspend fun deleteToken(tokenId: String) = tokensDao.deleteToken(tokenId)

    suspend fun findDuplicateToken(issuer: String, label: String, ignoreId: String?) =
        tokensDao.findDuplicateToken(issuer, label, ignoreId)
}