package com.ps.tokky.fragments

import android.os.Bundle
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.ps.tokky.BuildConfig
import com.ps.tokky.R
import com.ps.tokky.preference.AppLockPreference
import com.ps.tokky.preference.BiometricUnlockPreference
import com.ps.tokky.preference.DeleteTokensPreference
import com.ps.tokky.preference.ExportAccountsPreference
import com.ps.tokky.preference.ImportAccountsPreference
import com.ps.tokky.utils.AppPreferences
import com.ps.tokky.utils.AppPreferences.Companion.KEY_APP_LOCK
import com.ps.tokky.utils.AppPreferences.Companion.KEY_BIOMETRIC_UNLOCK
import com.ps.tokky.utils.AppPreferences.Companion.KEY_EXPORT_ACCOUNTS
import com.ps.tokky.utils.AppPreferences.Companion.KEY_IMPORT_ACCOUNTS

class PreferenceFragment : PreferenceFragmentCompat() {

    private val preferences by lazy { AppPreferences.getInstance(requireContext()) }

    private val appLockPreference: AppLockPreference? by lazy { findPreference(KEY_APP_LOCK) }
    private val biometricUnlockPreference: BiometricUnlockPreference? by lazy {
        findPreference(KEY_BIOMETRIC_UNLOCK)
    }

    private val importAccountsPreference: ImportAccountsPreference? by lazy {
        findPreference(KEY_IMPORT_ACCOUNTS)
    }
    private val exportAccountsPreference: ExportAccountsPreference? by lazy {
        findPreference(KEY_EXPORT_ACCOUNTS)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        appLockPreference?.init(parentFragmentManager)
        biometricUnlockPreference?.initBiometricPrompt(this)

        val areBiometricsAvailable = preferences.isBiometricAvailable()
        if (!areBiometricsAvailable) {
            biometricUnlockPreference?.isChecked = false
        }
        biometricUnlockPreference?.isEnabled = areBiometricsAvailable

        importAccountsPreference?.setupListeners(this)
        exportAccountsPreference?.setupListeners(this)

        if (BuildConfig.DEBUG) {
            addDeleteAllPreference()
        }
    }

    private fun addDeleteAllPreference() {
        val preferenceCategory = PreferenceCategory(preferenceScreen.context).apply {
            isIconSpaceReserved = false
            key = "kill_category"
        }
        preferenceScreen.addPreference(preferenceCategory)

        preferenceCategory.addPreference(
            DeleteTokensPreference(requireContext()).apply {
                isIconSpaceReserved = false
                layoutResource = R.layout.pref_kill_layout
                key = "key_kill"
                setTitle(R.string.pref_title_kill)
            }
        )
    }

    companion object {
        private const val TAG = "PreferenceFragment"
    }
}