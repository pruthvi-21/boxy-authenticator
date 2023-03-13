package com.ps.tokky.preference

import android.content.Context
import android.util.AttributeSet
import com.libx.ui.preference.SwitchPreference

class AppLockPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : SwitchPreference(context, attrs) {

    override fun onClick() {}

    fun click() {
        super.onClick()
    }
}