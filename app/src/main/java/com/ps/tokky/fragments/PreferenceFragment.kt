package com.ps.tokky.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.ps.tokky.R

class PreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}