package com.boxy.authenticator.data.repositories

import com.boxy.authenticator.data.models.Thumbnail
import com.boxy.authenticator.data.models.TokenEntry
import com.boxy.authenticator.domain.dao.TokenDao
import com.boxy.authenticator.domain.repository.TokenRepository
import kotlinx.datetime.Clock

class LocalTokenRepository(private val tokenDao: TokenDao) : TokenRepository {
    override fun getAllTokens(): List<TokenEntry> {
        return tokenDao.getAllTokens()
    }

    override fun findTokenWithId(tokenId: String): TokenEntry {
        return tokenDao.findTokenWithId(tokenId)
    }

    override fun findTokenWithName(issuer: String, label: String): TokenEntry? {
        return tokenDao.findTokenWithName(issuer, label)
    }

    override fun insertToken(token: TokenEntry) {
        tokenDao.insertToken(token)
    }

    override fun insertTokens(tokens: List<TokenEntry>) {
        tokenDao.insertTokens(tokens)
    }

    override fun deleteToken(tokenId: String) {
        tokenDao.deleteToken(tokenId)
    }

    override fun updateToken(tokenId: String, issuer: String, label: String, thumbnail: Thumbnail) {
        tokenDao.updateToken(
            tokenId = tokenId,
            issuer = issuer,
            label = label,
            thumbnail = thumbnail,
            updatedOn = Clock.System.now().toEpochMilliseconds()
        )
    }

    override fun replaceTokenWith(id: String, token: TokenEntry) {
        tokenDao.replaceTokenWith(id, token)
    }
}