package com.ps.tokky.preference

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceClickListener
import androidx.preference.PreferenceFragmentCompat
import com.ps.tokky.activities.transfer.ExportActivity
import com.ps.tokky.database.DBHelper

class ExportAccountsPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : Preference(context, attrs) {

    init {
        isEnabled = DBHelper.getInstance(context).getAll(false).size != 0
    }

    fun setupListeners(preferenceFragment: PreferenceFragmentCompat) {
        onPreferenceClickListener = OnPreferenceClickListener {
            preferenceFragment.startActivity(Intent(context, ExportActivity::class.java))
            true
        }
    }


}