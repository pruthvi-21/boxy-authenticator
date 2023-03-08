package com.ps.tokky.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.ps.tokky.R
import com.ps.tokky.databinding.ActivityEnterKeyDetailsBinding
import com.ps.tokky.models.TokenEntry
import com.ps.tokky.utils.*
import java.net.URI

class EnterKeyDetailsActivity : BaseActivity() {

    private val binding: ActivityEnterKeyDetailsBinding by lazy {
        ActivityEnterKeyDetailsBinding.inflate(layoutInflater)
    }

    private var shortAnimationDuration: Int = 0

    private val dbHelper = DBHelper.getInstance(this)

    private val editId: String? by lazy { intent.extras?.getString("id") }
    private val otpAuthUrl: String? by lazy { intent.extras?.getString("otpAuth") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Block screenshots
        if (!preferences.allowScreenshots) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }

        setContentView(binding.root)
        setSupportActionBar(binding.collapsingToolbar.toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_chevron_left)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = " "

        binding.collapsingToolbar.setTitle(R.string.title_enter_account_details)

        val editMode = editId != null || otpAuthUrl != null

        Log.i(TAG, "onCreate: In edit mode: $editMode")

        if (editMode) {
            try {
                val currentEntry = if (otpAuthUrl != null) TokenEntry(URI(otpAuthUrl!!))
                else dbHelper.getAllEntries(false).find { it.id == editId }

                binding.issuerField.editText.setText(currentEntry!!.issuer)
                binding.labelField.editText.setText(currentEntry.label)
                binding.labelField.editText.imeOptions = EditorInfo.IME_ACTION_DONE
                binding.secretKeyField.visibility = View.GONE
                binding.advLayoutSwitch.visibility = View.GONE
                binding.advLayout.advOptionsLayout.visibility = View.GONE

                binding.detailsSaveBtn.visibility = View.VISIBLE
                binding.issuerField.editText.addTextChangedListener(editModeTextWatcher)
                binding.labelField.editText.addTextChangedListener(editModeTextWatcher)
                binding.detailsSaveBtn.setOnClickListener {
                    hideKeyboard()

                    currentEntry.updateInfo(
                        issuer = binding.issuerField.editText.text.toString(),
                        label = binding.labelField.editText.text.toString()
                    )

                    updateEntryInDB(currentEntry)
                }
            } catch (exception: BadlyFormedURLException) {
                Log.e(TAG, "onCreate: ", exception)
                Toast.makeText(this, "URL is badly formed", Toast.LENGTH_SHORT).show()
            }
            return
        }

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        binding.issuerField.editText.addTextChangedListener(textWatcher)
        binding.labelField.editText.addTextChangedListener(textWatcher)
        binding.secretKeyField.editText.addTextChangedListener(textWatcher)
        binding.advLayout.advPeriodInputLayout.editText.addTextChangedListener(textWatcher)

        binding.detailsSaveBtn.isEnabled = false
        binding.advLayout.advPeriodInputLayout.editText.inputType = InputType.TYPE_CLASS_NUMBER
        binding.advLayout.advPeriodInputLayout.editText.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.secretKeyField.editText.imeOptions = EditorInfo.IME_ACTION_DONE

        binding.advLayoutSwitch.setOnClickListener {
            hideKeyboard()
            showAdvancedOptions(binding.advLayout.advOptionsLayout.visibility == View.GONE)
        }

        binding.advLayout.advPeriodInputLayout.editText.setText(Constants.DEFAULT_OTP_VALIDITY.toString())

        binding.detailsSaveBtn.setOnClickListener {
            hideKeyboard()

            try {
                val issuer = binding.issuerField.value
                val label = binding.labelField.value
                val secretKey = binding.secretKeyField.value.cleanSecretKey()
                val period = binding.advLayout.advPeriodInputLayout.value.toInt()

                val otpLength = when (binding.advLayout.otpLengthToggleGroup.checkedButtonId) {
                    binding.advLayout.btn4digits.id -> 4
                    binding.advLayout.btn8digits.id -> 8
                    else -> 6
                }

                val algo = findViewById<Button>(binding.advLayout.algoToggleGroup.checkedButtonId).text.toString()

                val token = TokenEntry(
                    id = null,
                    issuer = issuer,
                    label = label,
                    secretKey = secretKey,
                    otpLength = otpLength,
                    period = period,
                    algorithm = algo,
                    hash = null
                )

                addEntryInDB(token)
            } catch (exception: InvalidSecretKeyException) {
                Log.e(TAG, "onSaveDetails: Invalid Secret Key format")
                Toast.makeText(this, R.string.error_invalid_chars, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateEntryInDB(token: TokenEntry) {
        addEntryInDB(token, token.id)
    }

    private fun addEntryInDB(token: TokenEntry, oldId: String? = null) {
        try {
            if (oldId != null) {
                val isPresent = dbHelper.getAllEntries(false).find { it.id == oldId } != null
                if (isPresent && otpAuthUrl == null) dbHelper.removeEntry(oldId)
            }
            val success = dbHelper.addEntry(token)

            if (success) {
                setResult(Activity.RESULT_OK, Intent().putExtra("id", token.id))
                finish()
            } else Toast.makeText(this, R.string.error_db_entry_failed, Toast.LENGTH_SHORT).show()
        } catch (exception: TokenExistsInDBException) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Account already exists")
                .setMessage("You already have a account from '${token.issuer}'")
                .setPositiveButton("Replace") { _, _ ->
                    dbHelper.updateEntry(token)

                    setResult(Activity.RESULT_OK, Intent().putExtra("id", token.id))
                    finish()
                }
                .setNegativeButton("Rename") { _, _ ->
                    binding.detailsSaveBtn.isEnabled = false
                    binding.issuerField.textInputLayout.editText?.requestFocus()
                    binding.issuerField.editText.setSelection(token.issuer.length)
                    binding.issuerField.editText.showKeyboard(this, true)
                }
                .create()
                .show()
        }
    }

    private fun hideKeyboard() {
        binding.issuerField.editText.hideKeyboard(this)
        binding.labelField.editText.hideKeyboard(this)
        binding.secretKeyField.editText.hideKeyboard(this)
        binding.advLayout.advPeriodInputLayout.editText.hideKeyboard(this)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (binding.secretKeyField.editText.text.isEmpty()) {
                    onBackPressed()
                } else {
                    hideKeyboard()
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.enter_details_activity_dialog_back_title)
                        .setMessage(R.string.enter_details_activity_dialog_back_message)
                        .setPositiveButton(R.string.dialog_go_back) { _, _ -> onBackPressed() }
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .create()
                        .show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val textWatcher: TextWatcher =
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable) {
                val issuer = binding.issuerField.editText.text
                val label = binding.labelField.editText.text
                val secretKey = binding.secretKeyField.editText.text

                if (TextUtils.isEmpty(label) && TextUtils.isEmpty(issuer) || TextUtils.isEmpty(secretKey)) {
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

    private val editModeTextWatcher: TextWatcher =
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable) {
                val issuer = binding.issuerField.editText.text
                binding.detailsSaveBtn.isEnabled = !TextUtils.isEmpty(issuer)
            }
        }

    companion object {
        private const val TAG = "EnterKeyDetailsActivity"
    }
}
