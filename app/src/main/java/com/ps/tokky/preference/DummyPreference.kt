package com.ps.tokky.preference

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference

open class DummyPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : Preference(context, attrs, defStyleAttr, defStyleRes) {

    init {
        isEnabled = false
    }
}
