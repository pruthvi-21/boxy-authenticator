package com.ps.tokky.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base32
import com.ps.tokky.models.HashAlgorithm
import com.ps.tokky.models.OTPLength
import com.ps.tokky.models.TokenEntry

class DBHelper private constructor(
    context: Context
) : SQLiteOpenHelper(context, DBInfo.NAME, null, DBInfo.VERSION) {

    private val allEntries = ArrayList<TokenEntry>()

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE ${DBInfo.TABLE_KEYS} (" +
                    "${DBInfo.COL_ID} integer PRIMARY KEY AUTOINCREMENT, " +
                    "${DBInfo.COL_ISSUER} text, " +
                    "${DBInfo.COL_LABEL} text, " +
                    "${DBInfo.COL_SECRET_KEY} text, " +
                    "${DBInfo.COL_OTP_LENGTH} int, " +
                    "${DBInfo.COL_PERIOD} int, " +
                    "${DBInfo.COL_ALGORITHM} int)"
        )
    }

    fun addEntry(entry: TokenEntry): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
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
                val id = cursor.getInt(0)
                val issuer = cursor.getString(1)
                val label = cursor.getString(2)
                val secretKey = cursor.getString(3)
                val otpLength = OTPLength.values()
                    .find { it.id == cursor.getInt(4) }
                    ?: Constants.DEFAULT_OTP_LENGTH
                val period = cursor.getInt(5)
                val algo = HashAlgorithm.values()
                    .find { it.id == cursor.getInt(6) }
                    ?: Constants.DEFAULT_HASH_ALGORITHM

                allEntries.add(TokenEntry(id, issuer, label, Base32().decode(secretKey), otpLength, period, algo))
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
        val query = StringBuilder("UPDATE ${DBInfo.TABLE_KEYS} SET ")

        if (entry.issuer.isNotEmpty()) {
            query.append("${DBInfo.COL_ISSUER}='${entry.issuer}'")
        }
        if (entry.label.isNotEmpty()) {
            query.append(", ${DBInfo.COL_LABEL}='${entry.label}' ")
        }

        query.append("WHERE ${DBInfo.COL_ID}=${entry.dbID};")

        writableDatabase?.execSQL(query.toString())
        getAllEntries(true)
    }

    fun removeEntry(entry: TokenEntry) {
        writableDatabase?.execSQL("DELETE FROM ${DBInfo.TABLE_KEYS} WHERE ${DBInfo.COL_ID}=${entry.dbID};")
        allEntries.remove(entry)
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
        const val VERSION = 11
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