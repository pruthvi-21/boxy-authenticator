package com.ps.tokky.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.ps.tokky.R
import com.ps.tokky.databinding.ActivityEnterKeyDetailsBinding

class EnterKeyDetailsActivity : AppCompatActivity() {

    private val binding: ActivityEnterKeyDetailsBinding by lazy {
        ActivityEnterKeyDetailsBinding.inflate(layoutInflater)
    }

    private var shortAnimationDuration: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        binding.issuerField.editText.addTextChangedListener(textWatcher)
        binding.labelField.editText.addTextChangedListener(textWatcher)
        binding.secretKeyField.editText.addTextChangedListener(textWatcher)
        binding.advLayout.advPeriodInputLayout.editText.addTextChangedListener(textWatcher)
        binding.advLayout.advDigitsInputLayout.editText.addTextChangedListener(textWatcher)

        binding.detailsSaveBtn.isEnabled = false
        binding.advLayout.advPeriodInputLayout.editText.inputType = InputType.TYPE_CLASS_NUMBER
        binding.advLayout.advDigitsInputLayout.editText.inputType = InputType.TYPE_CLASS_NUMBER

        binding.advLayoutSwitch.setOnClickListener {
            showAdvancedOptions(binding.advLayout.advOptionsLayout.visibility == View.GONE)
        }
    }

    private fun showAdvancedOptions(show: Boolean) {
        val upArrow = R.drawable.ic_chevron_up
        val downArrow = R.drawable.ic_chevron_down
        if (show) {
            binding.advLayoutSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, upArrow, 0)
            binding.advLayoutSwitch.setText(R.string.label_hide_advanced_options)
            binding.advLayout.advOptionsLayout.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .alpha(1f)
            }
        } else {
            binding.advLayoutSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, downArrow, 0)
            binding.advLayoutSwitch.setText(R.string.label_view_advanced_options)
            binding.advLayout.advOptionsLayout.animate()
                .setDuration(shortAnimationDuration.toLong())
                .alpha(0f)

            Handler(Looper.getMainLooper()).postDelayed({
                binding.advLayout.advOptionsLayout.visibility = View.GONE
            }, shortAnimationDuration.toLong())
        }
    }

    private val textWatcher: TextWatcher =
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (TextUtils.isEmpty(binding.labelField.editText.text) &&
                    TextUtils.isEmpty(binding.issuerField.editText.text) ||
                    TextUtils.isEmpty(binding.secretKeyField.editText.text) ||
                    !isNonZeroIntegerInput(binding.advLayout.advDigitsInputLayout.textInputLayout)
                ) {
                    binding.detailsSaveBtn.isEnabled = false
                } else {
                    binding.detailsSaveBtn.isEnabled =
                        isNonZeroIntegerInput(binding.advLayout.advPeriodInputLayout.textInputLayout)
                }
            }

            private fun isNonZeroIntegerInput(til: TextInputLayout): Boolean {
                val text = til.editText?.text ?: return false
                return try {
                    !TextUtils.isEmpty(text) && text.toString().toInt() != 0
                } catch (e: NumberFormatException) {
                    false
                }
            }
        }
}
