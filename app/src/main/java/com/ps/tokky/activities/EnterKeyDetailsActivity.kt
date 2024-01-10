package com.ps.tokky.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.ps.tokky.R
import com.ps.tokky.databinding.DialogTitleDeleteWarningBinding
import com.ps.tokky.models.TokenEntry
import com.ps.tokky.utils.AccountEntryMethod
import com.ps.tokky.utils.BadlyFormedURLException
import com.ps.tokky.utils.Constants
import com.ps.tokky.utils.Constants.DELETE_SUCCESS_RESULT_CODE
import com.ps.tokky.utils.EmptyURLContentException
import com.ps.tokky.utils.InvalidSecretKeyException
import com.ps.tokky.utils.TokenExistsInDBException
import com.ps.tokky.utils.cleanSecretKey
import com.ps.tokky.utils.hideKeyboard
import com.ps.tokky.utils.showKeyboard
import com.ps.tokky.views.CollapsibleLinearLayout
import com.ps.tokky.views.ThumbnailController

class EnterKeyDetailsActivity : BaseActivity() {

    private var shortAnimationDuration: Long = 0

    private val editId: String? by lazy { intent.extras?.getString("id") }
    private val otpAuthUrl: String? by lazy { intent.extras?.getString("otpAuth") }

    private lateinit var toolbar: Toolbar
    private lateinit var thumbnailController: ThumbnailController
    private lateinit var issuerField: TextInputLayout
    private lateinit var labelField: TextInputLayout
    private lateinit var secretKeyLabel: TextView
    private lateinit var secretKeyField: TextInputLayout

    private lateinit var advancedOptionsCheckbox: CheckBox
    private lateinit var advancedLayoutView: CollapsibleLinearLayout
    private lateinit var periodField: TextInputLayout
    private lateinit var digitsField: TextInputLayout
    private lateinit var algorithmToggleGroup: MaterialButtonToggleGroup

    private lateinit var saveButton: Button

    private var currentEntry: TokenEntry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_enter_key_details)

        toolbar = findViewById(R.id.toolbar)
        thumbnailController = findViewById(R.id.thumbnail_controller)
        issuerField = findViewById(R.id.issuer_field)
        labelField = findViewById(R.id.label_field)
        secretKeyLabel = findViewById(R.id.secret_key_label)
        secretKeyField = findViewById(R.id.secret_key_field)
        periodField = findViewById(R.id.period_field)
        algorithmToggleGroup = findViewById(R.id.algorithm_toggle_group)
        digitsField = findViewById(R.id.digits_field)
        advancedOptionsCheckbox = findViewById(R.id.adv_options_switch)
        advancedLayoutView = findViewById(R.id.advanced_layout_view)
        saveButton = findViewById(R.id.details_save_btn)

        setSupportActionBar(toolbar)

        val editMode = editId != null || otpAuthUrl != null
        Log.i(TAG, "onCreate: In edit mode: $editMode")

        if (editMode) {
            try {
                currentEntry =
                    if (otpAuthUrl != null) TokenEntry.BuildFromUrl(otpAuthUrl).build()
                    else db.getAll(false).find { it.id == editId }

                currentEntry?.let {
                    supportActionBar?.title = "Update Details"

                    issuerField.editText?.setText(it.issuer)
                    labelField.editText?.setText(it.label)

                    thumbnailController.setInitials(currentEntry!!.issuer)
                    if (otpAuthUrl == null) {
                        thumbnailController.setThumbnailColor(it.thumbnailColor)
                        if (it.thumbnailIcon.isEmpty()) {
                            thumbnailController.thumbnailIcon = null
                        } else thumbnailController.thumbnailIcon = it.thumbnailIcon
                    }

                    labelField.editText?.imeOptions = EditorInfo.IME_ACTION_DONE
                    secretKeyLabel.visibility = View.GONE
                    secretKeyField.visibility = View.GONE
                    advancedOptionsCheckbox.visibility = View.GONE

                    saveButton.visibility = View.VISIBLE
                    issuerField.editText?.addTextChangedListener(editModeTextWatcher)
                    labelField.editText?.addTextChangedListener(editModeTextWatcher)
                    saveButton.setText(R.string.label_btn_update_account)
                    saveButton.setOnClickListener { _ ->
                        hideKeyboard()

                        it.updateInfo(
                            issuer = issuerField.editText?.text.toString(),
                            label = labelField.editText?.text.toString(),
                            thumbnailColor = thumbnailController.selectedColor,
                            thumbnailIcon = thumbnailController.thumbnailIcon ?: ""
                        )

                        updateEntryInDB(it)
                    }
                }

            } catch (exception: EmptyURLContentException) {
                Log.e(TAG, "onCreate: ", exception)
                Toast.makeText(this, "Empty URL", Toast.LENGTH_SHORT).show()
            } catch (exception: BadlyFormedURLException) {
                Log.e(TAG, "onCreate: ", exception)
                Toast.makeText(this, "URL is badly formed", Toast.LENGTH_SHORT).show()
            }
            return
        }

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        issuerField.editText!!.addTextChangedListener(textWatcher)
        labelField.editText!!.addTextChangedListener(textWatcher)
        secretKeyField.editText!!.addTextChangedListener(textWatcher)
        periodField.editText!!.addTextChangedListener(textWatcher)

        saveButton.isEnabled = false
        periodField.editText!!.imeOptions = EditorInfo.IME_ACTION_DONE
        secretKeyField.editText!!.imeOptions = EditorInfo.IME_ACTION_DONE

        advancedOptionsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            hideKeyboard()
            showAdvancedOptions(isChecked)
        }
        resetAdvanceFields()

        saveButton.setOnClickListener {
            hideKeyboard()

            val secretKey = secretKeyField.editText!!.text.toString().cleanSecretKey()
            try {
                val issuer = issuerField.editText!!.text.toString()
                val label = labelField.editText!!.text.toString()
                val period = periodField.editText!!.text.toString().toInt()
                val digits = digitsField.editText!!.text.toString().toInt()

                val algo =
                    findViewById<Button>(algorithmToggleGroup.checkedButtonId).text.toString()

                val token = TokenEntry.Builder()
                    .setIssuer(issuer)
                    .setLabel(label)
                    .setSecretKey(secretKey)
                    .setAlgorithm(algo)
                    .setPeriod(period)
                    .setDigits(digits)
                    .setAddedFrom(AccountEntryMethod.FORM)
                    .setThumbnailColor(thumbnailController.selectedColor)
                    .setThumbnailIcon(thumbnailController.thumbnailIcon ?: "")
                    .build()

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
                val isPresent = db.getAll(false).find { it.id == oldId } != null
                if (isPresent && otpAuthUrl == null) db.remove(oldId)
            }
            val success = db.add(token)

            if (success) {
                setResult(Activity.RESULT_OK, Intent().putExtra("id", token.id))
                finish()
            } else Toast.makeText(this, R.string.error_db_entry_failed, Toast.LENGTH_SHORT).show()
        } catch (exception: TokenExistsInDBException) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Account already exists")
                .setMessage("You already have a account from '${token.issuer}'")
                .setPositiveButton("Replace") { _, _ ->
                    db.update(token)

                    setResult(Activity.RESULT_OK, Intent().putExtra("id", token.id))
                    finish()
                }
                .setNegativeButton("Rename") { _, _ ->
                    saveButton.isEnabled = false
                    issuerField.editText?.requestFocus()
                    issuerField.editText?.setSelection(token.issuer.length)
                    issuerField.editText?.showKeyboard(this, true)
                }
                .create()
                .show()
        }
    }

    private fun hideKeyboard() {
        issuerField.editText!!.hideKeyboard(this)
        labelField.editText!!.hideKeyboard(this)
        secretKeyField.editText!!.hideKeyboard(this)
        periodField.editText!!.hideKeyboard(this)
    }

    private fun showAdvancedOptions(show: Boolean) {
        if (show) {
            advancedLayoutView.expand(50)
        } else {
            advancedLayoutView.collapse(50)
            Handler(Looper.getMainLooper()).postDelayed({
                resetAdvanceFields()
            }, 50)
        }
    }

    private fun resetAdvanceFields() {
        periodField.editText?.setText(Constants.DEFAULT_PERIOD.toString())
        digitsField.editText?.setText(Constants.DEFAULT_DIGITS.toString())
        algorithmToggleGroup.check(R.id.btn_sha1)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_token, menu)

        menu?.findItem(R.id.menu_token_delete)?.isEnabled = editId != null || otpAuthUrl != null

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (secretKeyField.editText!!.text.isEmpty()) {
                    onBackPressedDispatcher.onBackPressed()
                } else {
                    hideKeyboard()
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.enter_details_activity_dialog_back_title)
                        .setMessage(R.string.enter_details_activity_dialog_back_message)
                        .setPositiveButton(R.string.dialog_go_back) { _, _ -> onBackPressedDispatcher.onBackPressed() }
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .create()
                        .show()
                }
            }

            R.id.menu_token_delete -> {
                currentEntry?.let {
                    deleteToken(it)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteToken(entry: TokenEntry) {
        val titleViewBinding = DialogTitleDeleteWarningBinding.inflate(LayoutInflater.from(this))

        val ssb = SpannableStringBuilder(entry.issuer)
        ssb.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            entry.issuer.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        titleViewBinding.title.text =
            SpannableStringBuilder(getString(R.string.dialog_title_delete_token))
                .append(" ")
                .append(ssb)
                .append("?")

        MaterialAlertDialogBuilder(this)
            .setCustomTitle(titleViewBinding.root)
            .setMessage(R.string.dialog_message_delete_token)
            .setPositiveButton(R.string.dialog_remove) { _, _ ->
                db.remove(entry.id)
                setResult(DELETE_SUCCESS_RESULT_CODE)
                finish()
            }
            .setNegativeButton(R.string.dialog_cancel, null)
            .create()
            .show()
    }

    private val textWatcher: TextWatcher =
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable) {
                val issuer = issuerField.editText!!.text
                val secretKey = secretKeyField.editText!!.text

                if (TextUtils.isEmpty(issuer) || TextUtils.isEmpty(secretKey)) {
                    saveButton.isEnabled = false
                } else {
                    saveButton.isEnabled = isNonZeroIntegerInput(periodField)
                }

                thumbnailController.setInitials(issuer.toString())
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
                val issuer = issuerField.editText!!.text
                thumbnailController.setInitials(issuer.toString())
                saveButton.isEnabled = !TextUtils.isEmpty(issuer)
            }
        }

    companion object {
        private const val TAG = "EnterKeyDetailsActivity"
    }
}
