package com.ps.tokky.activities

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ps.tokky.database.DBHelper
import com.ps.tokky.utils.AppPreferences

open class BaseActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    protected val preferences by lazy { AppPreferences.getInstance(this) }
    protected val db by lazy { DBHelper.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferences.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            AppPreferences.KEY_ALLOW_SCREENSHOTS -> recreate()
            AppPreferences.KEY_SHOW_THUMBNAILS -> if (this is MainActivity) this.refresh(false)
        }
    }
}