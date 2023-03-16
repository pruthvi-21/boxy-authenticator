package com.ps.tokky.preference

import android.content.Context
import android.util.AttributeSet
import androidx.fragment.app.FragmentManager
import com.libx.ui.preference.SwitchPreference
import com.ps.tokky.R
import com.ps.tokky.fragments.ConfirmPinLayout
import com.ps.tokky.utils.AppPreferences
import com.ps.tokky.utils.CryptoUtils

class AppLockPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : SwitchPreference(context, attrs) {

    private var fragmentManager: FragmentManager? = null
    private val preferences = AppPreferences.getInstance(context)

    override fun onClick() {
        if (isChecked) disableAppLock() else enableAppLock()
    }

    fun init(fragmentManager: FragmentManager) {
        this.fragmentManager = fragmentManager
    }

    private fun enableAppLock() {
        val setupPinCallback = { firstPasscode: String ->
            val reconfirmCallback = { secondPasscode: String ->
                if (firstPasscode == secondPasscode) {
                    preferences.setPIN(CryptoUtils.hashPasscode(firstPasscode))
                    super.onClick()
                    true
                } else false
            }
            ConfirmPinLayout(R.string.settings_setup_reenter_pin_bottom_sheet_title, reconfirmCallback).show(
                fragmentManager!!,
                "PinReconfirmSheet"
            )
            true
        }

        ConfirmPinLayout(R.string.settings_setup_pin_bottom_sheet_title, setupPinCallback).show(
            fragmentManager!!,
            "PinSetupSheet"
        )
    }

    private fun disableAppLock() {
        fragmentManager ?: return
        val callback = { passcode: String ->
            val status = preferences.verifyPIN(CryptoUtils.hashPasscode(passcode))
            if (status) {
                super.onClick()
            }
            status
        }
        ConfirmPinLayout(
            R.string.settings_disable_pin_bottom_sheet_title,
            callback
        ).show(fragmentManager!!, "DisableAppLockBottomSheet")
    }
}