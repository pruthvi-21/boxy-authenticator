package com.ps.tokky.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ps.tokky.models.TokenEntry
import org.json.JSONObject

class DBHelper private constructor(
    context: Context
) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    private val allEntries = ArrayList<TokenEntry>()

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_KEYS ($COL_ID text PRIMARY KEY, $COL_DATA text)")
    }

    fun addEntry(entry: TokenEntry): Boolean {
        val te: TokenEntry? = allEntries.find { (it.issuer + it.label) == (entry.issuer + entry.label) }
        if (te != null) {
            throw TokenExistsInDBException()
        }
        val db = writableDatabase
        val data = entry.toJson().toString()

        val contentValues = ContentValues().apply {
            put(COL_ID, entry.id)
            put(COL_DATA, data)
        }

        val rowID = db.insert(TABLE_KEYS, null, contentValues)
        db.close()
        getAllEntries(true)

        return rowID != -1L
    }

    fun getAllEntries(refresh: Boolean): ArrayList<TokenEntry> {
        if (!refresh && allEntries.isNotEmpty()) return allEntries
        val cursor = readableDatabase.rawQuery("SELECT * FROM $TABLE_KEYS", null)

        allEntries.clear()

        if (cursor.moveToFirst()) {
            do {
                val obj = TokenEntry(
                    cursor.getString(0)!!,
                    JSONObject(cursor.getString(1)!!)
                )
                allEntries.add(obj)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return allEntries
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //Do the migration work here
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_KEYS")
        onCreate(db)
    }

    fun updateEntry(entry: TokenEntry) {
        val te: TokenEntry? = allEntries.find { it.id == entry.id }
        if (te != null) {
            throw TokenExistsInDBException()
        }
        writableDatabase?.execSQL("UPDATE $TABLE_KEYS SET $COL_DATA = '${entry.toJson()}' WHERE $COL_ID = '${entry.id}'")
    }

    fun removeEntry(id: String?) {
        id ?: return
        writableDatabase?.execSQL("DELETE FROM $TABLE_KEYS WHERE $COL_ID = '$id';")
        allEntries.removeIf { id == it.id }
    }

    companion object {
        const val DB_NAME = "auths"
        const val DB_VERSION = 14
        const val TABLE_KEYS = "auth_secret_keys"
        const val COL_ID = "id"
        const val COL_DATA = "data"

        private var instance: DBHelper? = null
        fun getInstance(context: Context): DBHelper {
            if (instance == null)
                instance = DBHelper(context)
            return instance!!
        }
    }

}