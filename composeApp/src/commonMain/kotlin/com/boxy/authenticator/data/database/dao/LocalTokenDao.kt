package com.boxy.authenticator.data.database.dao

import com.boxy.authenticator.db.TokenDatabase
import com.boxy.authenticator.db.TokenEntityQueries
import com.boxy.authenticator.db.Token_entry
import com.boxy.authenticator.domain.database.dao.TokenDao
import com.boxy.authenticator.domain.models.Thumbnail
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.models.enums.AccountEntryMethod
import com.boxy.authenticator.domain.models.otp.OtpInfo

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
                thumbnail = thumbnail.serialize(),
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
    thumbnail = Thumbnail.deserialize(thumbnail),
    otpInfo = OtpInfo.deserialize(otpInfo),
    createdOn = createdOn,
    updatedOn = updatedOn,
    addedFrom = AccountEntryMethod.valueOf(addedFrom),
)

private fun TokenEntityQueries.insertTokenEntry(token: TokenEntry) {
    insertToken(
        id = token.id,
        issuer = token.issuer,
        label = token.label,
        thumbnail = token.thumbnail.serialize(),
        otpInfo = token.otpInfo.serialize(),
        createdOn = token.createdOn,
        updatedOn = token.updatedOn,
        addedFrom = token.addedFrom.name,
    )
}