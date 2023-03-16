package com.ps.tokky.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.TextView
import com.ps.tokky.databinding.LayoutKeypadBinding

class KeypadLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs), OnClickListener {

    private val binding = LayoutKeypadBinding.inflate(LayoutInflater.from(context), this, true)

    val keypadButtons = arrayOf(
        binding.button1, binding.button2, binding.button3,
        binding.button4, binding.button5, binding.button6,
        binding.button7, binding.button8, binding.button9,
        binding.button0
    )

    var keypadKeyClickListener: OnKeypadKeyClickListener? = null

    init {
        for (btn in keypadButtons) {
            btn.addOnClickListener(this)
        }

        binding.buttonBackspace.addOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == binding.buttonBackspace.id) {
            keypadKeyClickListener?.onBackspaceClick()
        } else {
            val textView = v as TextView? ?: return
            keypadKeyClickListener?.onDigitClick(textView.text.toString())
        }
    }

    interface OnKeypadKeyClickListener {
        fun onDigitClick(digit: String)
        fun onBackspaceClick()
    }
}