package com.ps.tokky.preference

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.app.AppCompatDelegate
import com.libx.ui.preference.DropDownPreference
import com.libx.ui.preference.Preference.OnPreferenceChangeListener

class AppThemeControllerPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : DropDownPreference(context, attrs) {
    init {
        onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue ->
            when (newValue) {
                "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            true
        }
    }
}