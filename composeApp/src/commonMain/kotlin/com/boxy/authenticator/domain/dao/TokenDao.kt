package com.boxy.authenticator.domain.dao

import com.boxy.authenticator.data.models.Thumbnail
import com.boxy.authenticator.data.models.TokenEntry

interface TokenDao {

    fun getAllTokens(): List<TokenEntry>

    fun insertToken(token: TokenEntry)

    fun deleteToken(tokenId: String)

    fun findTokenWithId(tokenId: String): TokenEntry

    fun findTokenWithName(issuer: String, label: String): TokenEntry?

    fun insertTokens(tokens: List<TokenEntry>)

    fun updateToken(
        tokenId: String,
        issuer: String,
        label: String,
        thumbnail: Thumbnail,
        updatedOn: Long,
    )

    fun replaceTokenWith(id: String, token: TokenEntry)
}