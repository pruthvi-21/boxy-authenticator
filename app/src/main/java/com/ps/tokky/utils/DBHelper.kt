package com.ps.tokky.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ps.tokky.models.TokenEntry

class DBHelper private constructor(
    context: Context
) : SQLiteOpenHelper(context, DBInfo.NAME, null, DBInfo.VERSION) {

    private val allEntries = ArrayList<TokenEntry>()

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE ${DBInfo.TABLE_KEYS} (" +
                    "${DBInfo.COL_ID} text PRIMARY KEY, " +
                    "${DBInfo.COL_ISSUER} text, " +
                    "${DBInfo.COL_LABEL} text, " +
                    "${DBInfo.COL_SECRET_KEY} text, " +
                    "${DBInfo.COL_OTP_LENGTH} int, " +
                    "${DBInfo.COL_PERIOD} int, " +
                    "${DBInfo.COL_ALGORITHM} int)"
        )
    }

    fun addEntry(entry: TokenEntry): Boolean {
        val te: TokenEntry? = allEntries.find { it.dbID == entry.dbID }
        if (te != null) {
            throw TokenExistsInDBException()
        }
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(DBInfo.COL_ID, entry.dbID)
            put(DBInfo.COL_ISSUER, entry.issuer)
            put(DBInfo.COL_LABEL, entry.label)
            put(DBInfo.COL_SECRET_KEY, entry.secretKeyEncoded)
            put(DBInfo.COL_OTP_LENGTH, entry.otpLength.id)
            put(DBInfo.COL_PERIOD, entry.period)
            put(DBInfo.COL_ALGORITHM, entry.algorithm.id)
        }

        val rowID = db.insert(DBInfo.TABLE_KEYS, null, contentValues)
        db.close()
        getAllEntries(true)

        return rowID != -1L
    }

    fun getAllEntries(refresh: Boolean): ArrayList<TokenEntry> {
        if (!refresh && allEntries.isNotEmpty()) return allEntries
        val cursor = readableDatabase.rawQuery("SELECT * FROM ${DBInfo.TABLE_KEYS} ORDER BY ${DBInfo.COL_ISSUER} ASC", null)

        allEntries.clear()

        if (cursor.moveToFirst()) {
            do {
                val obj = TokenEntry.Builder()
                    .buildFromCursor(cursor)
                if (obj != null)
                    allEntries.add(obj)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return allEntries
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //Do the migration work here
        db?.execSQL("DROP TABLE IF EXISTS ${DBInfo.TABLE_KEYS}")
        onCreate(db)
    }

    fun updateEntry(entry: TokenEntry) {
        removeEntry(entry)
        addEntry(entry)
        getAllEntries(true)
    }

    fun removeEntry(entry: TokenEntry) {
        writableDatabase?.execSQL("DELETE FROM ${DBInfo.TABLE_KEYS} WHERE ${DBInfo.COL_ID}='${entry.dbID}';")
        allEntries.removeIf { entry.dbID == it.dbID }
    }

    companion object {
        private var instance: DBHelper? = null
        fun getInstance(context: Context): DBHelper {
            if (instance == null)
                instance = DBHelper(context)
            return instance!!
        }
    }

    private object DBInfo {
        const val NAME = "auths"
        const val VERSION = 13
        const val TABLE_KEYS = "auth_secret_keys"
        const val COL_ID = "id"
        const val COL_ISSUER = "issuer"
        const val COL_LABEL = "label"
        const val COL_SECRET_KEY = "secret_key"
        const val COL_ALGORITHM = "algorithm"
        const val COL_PERIOD = "period"
        const val COL_OTP_LENGTH = "otp_length"
    }
}