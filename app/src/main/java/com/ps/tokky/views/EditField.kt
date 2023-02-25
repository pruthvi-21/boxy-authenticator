package com.ps.tokky.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.FrameLayout
import com.google.android.material.textfield.TextInputLayout
import com.ps.tokky.R
import com.ps.tokky.databinding.LayoutEditFieldBinding
import com.ps.tokky.utils.ColorUtils

class EditField(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding = LayoutEditFieldBinding.inflate(LayoutInflater.from(context), this, true)

    val textInputLayout: TextInputLayout = binding.til
    val editText: EditText = textInputLayout.editText!!

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.EditField)

        editText.hint = ta.getString(R.styleable.EditField_hint)
        binding.tilIcon.setImageResource(ta.getResourceId(R.styleable.EditField_icon, 0))

        binding.container.backgroundTintList = ColorStateList.valueOf(ColorUtils.primaryTintedBackground(context))
        binding.til.boxBackgroundColor = ColorUtils.primaryTintedBackground(context)

        ta.recycle()
    }

    val value: String
        get() {
            return editText.text.toString()
        }
}