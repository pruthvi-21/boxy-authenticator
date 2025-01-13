package com.boxy.authenticator.data.preferences

interface PreferenceStore {
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String? = null): String?

    fun putInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int = 0): Int

    fun remove(key: String)
    fun clear()
}