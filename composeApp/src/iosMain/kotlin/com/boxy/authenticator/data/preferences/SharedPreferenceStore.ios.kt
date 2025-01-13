package com.boxy.authenticator.data.preferences

import platform.Foundation.NSUserDefaults

actual class SharedPreferenceStore : PreferenceStore {

    private val userDefaults = NSUserDefaults.standardUserDefaults()

    override fun putBoolean(key: String, value: Boolean) {
        userDefaults.setBool(value, key)
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return userDefaults.boolForKey(key).takeIf { userDefaults.objectForKey(key) != null }
            ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        userDefaults.setObject(value, key)
    }

    override fun getString(key: String, defaultValue: String?): String? {
        return userDefaults.stringForKey(key) ?: defaultValue
    }

    override fun putInt(key: String, value: Int) {
        userDefaults.setInteger(value.toLong(), key)
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return userDefaults.integerForKey(key).toInt().takeIf { userDefaults.objectForKey(key) != null }
            ?: defaultValue
    }

    override fun remove(key: String) {
        userDefaults.removeObjectForKey(key)
    }

    override fun clear() {
        userDefaults.dictionaryRepresentation().keys.forEach { key ->
            userDefaults.removeObjectForKey(key as String)
        }
    }
}