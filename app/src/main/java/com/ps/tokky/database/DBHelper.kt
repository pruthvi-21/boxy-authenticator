package com.ps.tokky.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ps.tokky.models.AuthEntry
import com.ps.tokky.models.HashAlgorithm

class DBHelper(context: Context) : SQLiteOpenHelper(context, DBInfo.NAME, null, DBInfo.VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "create table ${DBInfo.TABLE_KEYS} (" +
                    "${DBInfo.COL_ID} integer primary key autoincrement, " +
                    "${DBInfo.COL_ISSUER} text, " +
                    "${DBInfo.COL_LABEL} text, " +
                    "${DBInfo.COL_SECRET_KEY} text, " +
                    "${DBInfo.COL_DIGITS} int, " +
                    "${DBInfo.COL_PERIOD} int, " +
                    "${DBInfo.COL_ALGORITHM} int)"
        )
    }

    fun addEntry(entry: AuthEntry): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(DBInfo.COL_ISSUER, entry.issuer)
            put(DBInfo.COL_LABEL, entry.label)
            put(DBInfo.COL_SECRET_KEY, entry.secretKey)
            put(DBInfo.COL_DIGITS, entry.digits)
            put(DBInfo.COL_PERIOD, entry.period)
            put(DBInfo.COL_ALGORITHM, HashAlgorithm.values().indexOf(entry.algo))
        }

        val rowID = db.insert(DBInfo.TABLE_KEYS, null, contentValues)
        db.close()

        return rowID != -1L
    }

    fun getAllEntries(): List<AuthEntry> {
        val cursor = readableDatabase.rawQuery("select * from ${DBInfo.TABLE_KEYS}", null)

        val list = ArrayList<AuthEntry>()

        if (cursor.moveToFirst()) {
            do {
                list.add(
                    AuthEntry(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getInt(5),
                        HashAlgorithm.values()[cursor.getInt(6)]
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return list
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //Do the migration work here
        db?.execSQL("DROP TABLE IF EXISTS ${DBInfo.TABLE_KEYS}")
        onCreate(db)
    }

    private object DBInfo {
        const val NAME = "auths"
        const val VERSION = 6
        const val TABLE_KEYS = "auth_secret_keys"
        const val COL_ID = "id"
        const val COL_ISSUER = "issuer"
        const val COL_LABEL = "label"
        const val COL_SECRET_KEY = "secret_key"
        const val COL_ALGORITHM = "algorithm"
        const val COL_PERIOD = "period"
        const val COL_DIGITS = "digits"
    }
}