package com.ps.tokky.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.LinearLayout.LayoutParams
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.ps.tokky.R
import com.ps.tokky.database.DBHelper
import com.ps.tokky.databinding.ActivityEnterKeyDetailsBinding
import com.ps.tokky.models.AuthEntry
import com.ps.tokky.models.HashAlgorithm
import com.ps.tokky.models.OTPLength
import com.ps.tokky.utils.Constants

class EnterKeyDetailsActivity : AppCompatActivity() {

    private val TAG = "EnterKeyDetailsActivity"

    private val binding: ActivityEnterKeyDetailsBinding by lazy {
        ActivityEnterKeyDetailsBinding.inflate(layoutInflater)
    }

    private var shortAnimationDuration: Int = 0

    private val dbHelper = DBHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        binding.issuerField.editText.addTextChangedListener(textWatcher)
        binding.labelField.editText.addTextChangedListener(textWatcher)
        binding.secretKeyField.editText.addTextChangedListener(textWatcher)
        binding.advLayout.advPeriodInputLayout.editText.addTextChangedListener(textWatcher)

        binding.detailsSaveBtn.isEnabled = false
        binding.advLayout.advPeriodInputLayout.editText.inputType = InputType.TYPE_CLASS_NUMBER

        binding.advLayoutSwitch.setOnClickListener {
            showAdvancedOptions(binding.advLayout.advOptionsLayout.visibility == View.GONE)
        }

        binding.advLayout.advPeriodInputLayout.editText.setText("30")

        inflateAlgorithmMethods()
        inflateOTPLengthToggleLayout()

        binding.detailsSaveBtn.setOnClickListener {
            val issuer = binding.issuerField.value
            val label = binding.labelField.value
            val secretKey = binding.secretKeyField.value
            val otpLength = OTPLength
                .values()
                .find { it.resId == binding.advLayout.otpLengthToggleGroup.checkedButtonId }
            val period = binding.advLayout.advPeriodInputLayout.value.toInt()
            val algo = HashAlgorithm
                .values()
                .find { it.resId == binding.advLayout.algoToggleGroup.checkedButtonId }

            otpLength ?: Log.e(TAG, "No value selected for OTP Length")
            algo ?: Log.e(TAG, "No value selected for Hash Algorithm")

            if (otpLength == null || algo == null) {
                Toast.makeText(this, R.string.error_saving_details, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newEntry = AuthEntry(issuer, label, secretKey, otpLength, period, algo)
            val success = dbHelper.addEntry(newEntry)

            if (success) finish()
            else Toast.makeText(this, R.string.error_db_entry_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun inflateAlgorithmMethods() {
        for (algo in HashAlgorithm.values()) {
            val button = MaterialButton(this, null, R.attr.buttonGroupButtonStyle).apply {
                id = algo.resId
                text = algo.name
            }
            binding.advLayout.algoToggleGroup.addView(button)
            (button.layoutParams as LayoutParams).weight = 1f
        }

        binding.advLayout.algoToggleGroup.check(Constants.DEFAULT_HASH_ALGORITHM.resId)
    }

    private fun inflateOTPLengthToggleLayout() {
        for (len in OTPLength.values()) {
            val button = MaterialButton(this, null, R.attr.buttonGroupButtonStyle)
            button.id = len.resId
            button.text = len.title
            binding.advLayout.otpLengthToggleGroup.addView(button)
            (button.layoutParams as LayoutParams).weight = 1f
        }

        binding.advLayout.otpLengthToggleGroup.check(Constants.DEFAULT_OTP_LENGTH.resId)
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
                    TextUtils.isEmpty(binding.secretKeyField.editText.text)
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
