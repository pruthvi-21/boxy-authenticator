package com.ps.tokky.preferences

import android.content.Context
import android.util.AttributeSet

class AppLockPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : MaterialSwitchPreference(context, attrs) {

    override fun onClick() {}

    fun click() {
        super.onClick()
    }
}