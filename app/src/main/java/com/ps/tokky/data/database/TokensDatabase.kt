package com.ps.tokky.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ps.tokky.data.models.TokenEntry

@Database(entities = [TokenEntry::class], version = 1)
@TypeConverters(Converters::class)
abstract class TokensDatabase : RoomDatabase() {

    abstract fun tokensDao(): TokensDao

    companion object {
        @Volatile
        private var instance: TokensDatabase? = null

        fun getInstance(context: Context): TokensDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    TokensDatabase::class.java,
                    "tokens_database"
                ).build().also { instance = it }
            }
        }
    }
}