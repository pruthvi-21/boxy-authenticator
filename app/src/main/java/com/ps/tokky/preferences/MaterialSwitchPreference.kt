package com.ps.tokky.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.TypedArrayUtils
import androidx.preference.SwitchPreference
import com.ps.tokky.R

open class MaterialSwitchPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @SuppressLint("RestrictedApi")
    defStyleAttr: Int = TypedArrayUtils.getAttr(
        context,
        androidx.preference.R.attr.switchPreferenceStyle,
        android.R.attr.switchPreferenceStyle
    ),
    defStyleRes: Int = 0
) : SwitchPreference(context, attrs, defStyleAttr, defStyleRes) {

    init {
        isIconSpaceReserved = false
        widgetLayoutResource = R.layout.m3_switch
    }
}