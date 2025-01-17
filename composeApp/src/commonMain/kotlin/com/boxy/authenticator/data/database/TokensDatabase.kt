package com.boxy.authenticator.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.boxy.authenticator.data.models.TokenEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

const val dbName = "tokens.db"

@Database(entities = [TokenEntry::class], version = 1)
@ConstructedBy(TokensDatabaseConstructor::class)
@TypeConverters(Converters::class, ThumbnailConverter::class)
abstract class TokensDatabase : RoomDatabase() {
    abstract fun getTokensDao(): TokensDao

    companion object {
        fun build(
            builder: Builder<TokensDatabase>,
        ): TokensDatabase {
            return builder
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
        }
    }
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object TokensDatabaseConstructor : RoomDatabaseConstructor<TokensDatabase> {
    override fun initialize(): TokensDatabase
}
