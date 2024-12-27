package com.ps.tokky.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.helpers.SecureKeyManager
import net.sqlcipher.database.SupportFactory

@Database(entities = [TokenEntry::class], version = 1)
@TypeConverters(Converters::class)
abstract class TokensDatabase : RoomDatabase() {

    abstract fun tokensDao(): TokensDao

    companion object {
        @Volatile
        private var instance: TokensDatabase? = null

        fun getInstance(context: Context): TokensDatabase {
            return instance ?: synchronized(this) {
                instance ?: try {
                    createDatabase(context).also { instance = it }
                } catch (e: Exception) {
                    Log.e("TokensDatabase", "Error creating database", e)
                    throw DatabaseInitializationException("Error initializing the database", e)
                }
            }
        }

        private fun createDatabase(context: Context): TokensDatabase {
            return try {
                val key = SecureKeyManager.databaseKey
                Room.databaseBuilder(
                    context.applicationContext,
                    TokensDatabase::class.java,
                    "tokens_database"
                )
                    .openHelperFactory(SupportFactory(key))
                    .build()
            } catch (e: Exception) {
                Log.e("TokensDatabase", "Error creating database with the provided key", e)
                throw DatabaseInitializationException("Error creating database", e)
            }
        }
    }

    class DatabaseInitializationException(message: String, cause: Throwable) :
        Exception(message, cause)
}