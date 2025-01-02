package com.ps.tokky.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ps.tokky.data.models.TokenEntry

@Dao
interface TokensDao {

    @Query("SELECT * FROM token_entry")
    suspend fun getAllTokens(): List<TokenEntry>

    @Query("SELECT * FROM token_entry WHERE id = :tokenId")
    suspend fun findTokenWithId(tokenId: String): TokenEntry

    @Query(
        """
            SELECT * FROM token_entry 
            WHERE issuer = :issuer COLLATE NOCASE 
            AND label = :label COLLATE NOCASE
            LIMIT 1
        """
    )
    suspend fun findTokenWithName(issuer: String, label: String): TokenEntry?

    @Insert
    suspend fun insertTokens(tokens: List<TokenEntry>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertToken(token: TokenEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertToken(token: TokenEntry)

    @Query("DELETE FROM token_entry WHERE id = :tokenId")
    suspend fun deleteToken(tokenId: String)
}