package com.ps.tokky.views

import android.content.Context
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.widget.AppCompatTextView
import com.ps.tokky.R

class KeypadButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs, R.attr.keypadButtonStyle), OnClickListener {

    private val listeners = ArrayList<OnClickListener>()

    init {
        setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        listeners.forEach { it.onClick(v) }
    }

    fun addOnClickListener(listener: OnClickListener) {
        listeners.add(listener)
    }

    fun removeOnClickListener(listener: OnClickListener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener)
        }
    }
}