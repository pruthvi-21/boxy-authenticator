package com.boxy.authenticator.data.database

import com.boxy.authenticator.data.models.Thumbnail
import com.boxy.authenticator.data.models.TokenEntry
import com.boxy.authenticator.db.TokenDatabase
import com.boxy.authenticator.db.TokenEntityQueries
import com.boxy.authenticator.db.Token_entry
import com.boxy.authenticator.domain.dao.TokenDao

class LocalTokenDao(database: TokenDatabase) : TokenDao {
    private val queries: TokenEntityQueries = database.tokenEntityQueries

    override fun getAllTokens(): List<TokenEntry> {
        return queries.getAllTokens()
            .executeAsList()
            .map { it.toTokenEntry() }
    }

    override fun insertToken(token: TokenEntry) {
        queries.transaction {
            queries.insertTokenEntry(token)
        }
    }

    override fun deleteToken(tokenId: String) {
        queries.transaction {
            queries.deleteToken(tokenId)
        }
    }

    override fun findTokenWithId(tokenId: String): TokenEntry {
        return queries.findTokenWithId(tokenId).executeAsOne().toTokenEntry()
    }

    override fun findTokenWithName(issuer: String, label: String): TokenEntry? {
        return queries.findTokenWithName(issuer, label).executeAsOneOrNull()?.toTokenEntry()
    }

    override fun insertTokens(tokens: List<TokenEntry>) {
        queries.transaction {
            tokens.forEach { token ->
                queries.insertTokenEntry(token)
            }
        }
    }

    override fun updateToken(
        tokenId: String,
        issuer: String,
        label: String,
        thumbnail: Thumbnail,
        updatedOn: Long,
    ) {
        queries.transaction {
            queries.updateToken(
                issuer = issuer,
                label = label,
                thumbnail = Converters.fromThumbnail(thumbnail),
                updatedOn = updatedOn,
                id = tokenId
            )
        }
    }

    override fun replaceTokenWith(id: String, token: TokenEntry) {
        queries.transaction {
            queries.deleteToken(id)
            queries.insertTokenEntry(token)
        }
    }
}

private fun Token_entry.toTokenEntry() = TokenEntry(
    id = id,
    issuer = issuer,
    label = label,
    thumbnail = Converters.toThumbnail(thumbnail),
    otpInfo = Converters.toOtpInfo(Converters.toJsonObject(otpInfo)),
    createdOn = createdOn,
    updatedOn = updatedOn,
    addedFrom = Converters.toAccountEntryMethod(addedFrom)
)

private fun TokenEntityQueries.insertTokenEntry(token: TokenEntry) {
    insertToken(
        id = token.id,
        issuer = token.issuer,
        label = token.label,
        thumbnail = Converters.fromThumbnail(token.thumbnail),
        otpInfo = Converters.fromJsonObject(Converters.fromOtpInfo(token.otpInfo)),
        createdOn = token.createdOn,
        updatedOn = token.updatedOn,
        addedFrom = Converters.fromAccountEntryMethod(token.addedFrom)
    )
}