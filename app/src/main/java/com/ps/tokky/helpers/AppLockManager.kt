package com.ps.tokky.helpers

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.ps.tokky.R
import com.ps.tokky.ui.fragments.ConfirmPinLayout
import com.ps.tokky.utils.CryptoUtils

class AppLockManager {

    fun enableAppLock(context: Context, settings: AppSettings, onDone: (Boolean) -> Unit) {
        val fragmentManager = (context as AppCompatActivity).supportFragmentManager

        val setupPinCallback = { firstPasscode: String ->
            val reconfirmCallback = { secondPasscode: String ->
                val result = if (firstPasscode == secondPasscode) {
                    settings.setPasscodeHash(CryptoUtils.hashPasscode(firstPasscode))
                    settings.setAppLockEnabled(true)
                    true
                } else false
                onDone(result)
                result
            }
            ConfirmPinLayout(R.string.settings_setup_reenter_pin_bottom_sheet_title, reconfirmCallback)
                .show(fragmentManager, "PinReconfirmSheet")
            true
        }

        ConfirmPinLayout(R.string.settings_setup_pin_bottom_sheet_title, setupPinCallback)
            .show(fragmentManager, "PinSetupSheet")
    }

    fun disableAppLock(context: Context, settings: AppSettings, onDone: (Boolean) -> Unit) {
        val fragmentManager = (context as AppCompatActivity).supportFragmentManager

        val callback = { passcode: String ->
            val currentPasscodeHash = settings.getPasscodeHash()
            val enteredPasscodeHash = CryptoUtils.hashPasscode(passcode)
            val status = currentPasscodeHash == enteredPasscodeHash
            if (status) {
                settings.setAppLockEnabled(false)
            }
            onDone(status)
            status
        }

        ConfirmPinLayout(R.string.settings_disable_pin_bottom_sheet_title, callback)
            .show(fragmentManager, "DisableAppLockBottomSheet")
    }
}