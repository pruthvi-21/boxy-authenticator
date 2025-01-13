package com.boxy.authenticator.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getTokensDatabaseBuilder(context: Context): RoomDatabase.Builder<TokensDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(dbName)
    return Room.databaseBuilder<TokensDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}