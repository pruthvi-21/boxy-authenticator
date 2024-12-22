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
    suspend fun getTokenWithId(tokenId: String): TokenEntry

    @Insert
    suspend fun insertAccounts(accounts: List<TokenEntry>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertToken(token: TokenEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertToken(token: TokenEntry)

    @Query("DELETE FROM token_entry WHERE id = :tokenId")
    suspend fun deleteToken(tokenId: String)

    @Query(
        """
            SELECT * FROM token_entry 
            WHERE issuer = :issuer COLLATE NOCASE 
            AND label = :label COLLATE NOCASE
            AND (:ignoreId IS NULL OR id != :ignoreId)
            LIMIT 1
        """
    )
    suspend fun findDuplicateToken(issuer: String, label: String, ignoreId: String?): TokenEntry?
}