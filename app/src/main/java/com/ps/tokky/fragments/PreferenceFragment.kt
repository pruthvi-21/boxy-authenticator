package com.ps.tokky.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.ps.tokky.BuildConfig
import com.ps.tokky.R
import com.ps.tokky.preference.AppLockPreference
import com.ps.tokky.preference.BiometricUnlockPreference
import com.ps.tokky.preference.DeleteTokensPreference
import com.ps.tokky.preference.ExportAccountsPreference
import com.ps.tokky.preference.ImportAccountsPreference
import com.ps.tokky.utils.AppSettings

class PreferenceFragment : PreferenceFragmentCompat() {

    private val appLockPreference: AppLockPreference? by lazy {
        findPreference(getString(R.string.key_app_lock))
    }
    private val biometricUnlockPreference: BiometricUnlockPreference? by lazy {
        findPreference(getString(R.string.key_biometric_unlock))
    }
    private val useBlackThemePreference: SwitchPreference? by lazy {
        findPreference(getString(R.string.key_use_black_theme))
    }
    private val importAccountsPreference: ImportAccountsPreference? by lazy {
        findPreference(getString(R.string.key_import_accounts))
    }
    private val exportAccountsPreference: ExportAccountsPreference? by lazy {
        findPreference(getString(R.string.key_export_accounts))
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        appLockPreference?.init(parentFragmentManager)
        biometricUnlockPreference?.initBiometricPrompt(this)

        val areBiometricsAvailable = AppSettings.isBiometricAvailable(requireContext())
        if (!areBiometricsAvailable) {
            biometricUnlockPreference?.isChecked = false
        }
        biometricUnlockPreference?.isEnabled = areBiometricsAvailable

        useBlackThemePreference?.setOnPreferenceChangeListener { _, _ ->
            Handler(Looper.getMainLooper()).postDelayed({ activity?.recreate() }, 300)
            true
        }

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