package com.ps.tokky.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ps.tokky.models.TokenEntry
import com.ps.tokky.utils.CryptoUtils
import com.ps.tokky.utils.TokenExistsInDBException
import org.json.JSONObject
import javax.crypto.SecretKey

class DBHelper private constructor(
    context: Context
) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION), DBInterface<TokenEntry> {

    private val allEntries = ArrayList<TokenEntry>()

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_KEYS ($COL_ID text PRIMARY KEY, $COL_DATA text)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_KEYS")
        onCreate(db)
    }

    override fun add(item: TokenEntry): Boolean {
        val te: TokenEntry? =
            allEntries.find { (it.issuer + it.label) == (item.issuer + item.label) }
        if (te != null) {
            throw TokenExistsInDBException()
        }
        val db = writableDatabase
        val data = item.toJson().toString()
        val cipher = CryptoUtils.encryptData(data, secretKey)

        val contentValues = ContentValues().apply {
            put(COL_ID, item.id)
            put(COL_DATA, cipher)
        }

        val rowID = db.insert(TABLE_KEYS, null, contentValues)
        db.close()
        getAll(true)

        return rowID != -1L
    }

    override fun update(item: TokenEntry) {
        val te: TokenEntry? = allEntries.find { it.id == item.id }
        if (te != null) {
            throw TokenExistsInDBException()
        }
        writableDatabase?.execSQL("update $TABLE_KEYS set $COL_DATA = '${item.toJson()}' WHERE $COL_ID = '${item.id}'")
    }

    override fun get(itemId: String): TokenEntry? {
        val cursor =
            readableDatabase.rawQuery("select * from $TABLE_KEYS where $COL_ID = '$itemId'", null)
        return if (cursor.moveToFirst()) {
            buildTokenFromCursor(cursor)
        } else null
    }

    override fun getAll(reload: Boolean): ArrayList<TokenEntry> {
        if (!reload && allEntries.isNotEmpty()) return allEntries
        val cursor = readableDatabase.rawQuery("select * from $TABLE_KEYS", null)

        allEntries.clear()

        if (cursor.moveToFirst()) {
            do {
                allEntries.add(buildTokenFromCursor(cursor))
            } while (cursor.moveToNext())
        }

//        val valid = TokenEntry.validateHash(allEntries)
//        Log.i(TAG, "getAllEntries: validated tokens hash: $valid")

        cursor.close()
        return allEntries
    }

    override fun remove(itemId: String) {
        writableDatabase?.execSQL("DELETE FROM $TABLE_KEYS WHERE $COL_ID = '$itemId';")
        allEntries.removeIf { itemId == it.id }
    }

    private fun buildTokenFromCursor(cursor: Cursor): TokenEntry {
        val id = cursor.getString(0)
        val cipher = cursor.getString(1)
        val data = CryptoUtils.decryptData(cipher, secretKey)
        val jsonObj = JSONObject(data)

        return TokenEntry.BuildFromDBJson(id, jsonObj).build()
    }

    fun getEntriesWithIDs(list: List<String?>): List<TokenEntry?> {
        return list.map { allEntries.find { it1 -> it == it1.id } }
    }

    companion object {
        private const val TAG = "DBHelper"

        const val DB_NAME = "auths"
        const val DB_VERSION = 14
        const val TABLE_KEYS = "auth_secret_keys"
        const val COL_ID = "id"
        const val COL_DATA = "data"

        private var instance: DBHelper? = null
        private var secretKey: SecretKey? = null
        fun getInstance(context: Context): DBHelper {
            if (instance == null) {
                instance = DBHelper(context)
                secretKey = CryptoUtils.getSecretKey()
            }
            return instance!!
        }
    }
}