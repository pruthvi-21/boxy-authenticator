package com.boxy.authenticator.domain.repository

import com.boxy.authenticator.data.models.Thumbnail
import com.boxy.authenticator.data.models.TokenEntry

interface TokenRepository {

    fun getAllTokens(): List<TokenEntry>

    fun findTokenWithId(tokenId: String): TokenEntry

    fun findTokenWithName(issuer: String, label: String): TokenEntry?

    fun insertTokens(tokens: List<TokenEntry>)

    fun insertToken(token: TokenEntry)

    fun deleteToken(tokenId: String)

    fun updateToken(tokenId: String, issuer: String, label: String, thumbnail: Thumbnail)

    fun replaceTokenWith(id: String, token: TokenEntry)
}