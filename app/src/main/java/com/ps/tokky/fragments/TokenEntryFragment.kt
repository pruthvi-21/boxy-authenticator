package com.ps.tokky.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.ps.tokky.R
import com.ps.tokky.databinding.DialogTitleDeleteWarningBinding
import com.ps.tokky.databinding.FragmentTokenEntryBinding
import com.ps.tokky.models.TokenEntry
import com.ps.tokky.utils.AccountEntryMethod
import com.ps.tokky.utils.BadlyFormedURLException
import com.ps.tokky.utils.Constants
import com.ps.tokky.utils.EmptyURLContentException
import com.ps.tokky.utils.InvalidSecretKeyException
import com.ps.tokky.utils.cleanSecretKey
import com.ps.tokky.utils.hideKeyboard
import com.ps.tokky.utils.showKeyboard
import com.ps.tokky.viewmodels.TokensViewModel
import com.ps.tokky.views.CollapsibleLinearLayout
import com.ps.tokky.views.ThumbnailController
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class TokenEntryFragment : Fragment() {
    private lateinit var binding: FragmentTokenEntryBinding

    private val otpAuthUrl: String? by lazy { arguments?.getString("otpAuth") }

    private val navController: NavController by lazy { findNavController() }

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

    private val tokensViewModel: TokensViewModel by activityViewModels()

    private val currentCode = UUID.randomUUID().toString()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTokenEntryBinding.inflate(layoutInflater, container, false)

        toolbar = binding.toolbar
        thumbnailController = binding.thumbnailController
        issuerField = binding.issuerField
        labelField = binding.labelField
        secretKeyLabel = binding.secretKeyLabel
        secretKeyField = binding.secretKeyField
        periodField = binding.advLayout.periodField
        algorithmToggleGroup = binding.advLayout.algorithmToggleGroup
        digitsField = binding.advLayout.digitsField
        advancedOptionsCheckbox = binding.advOptionsSwitch
        advancedLayoutView = binding.advLayout.advancedLayoutView
        saveButton = binding.detailsSaveBtn

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val editMode = tokensViewModel.tokenToEdit != null
        Log.i(TAG, "onCreate: In edit mode: $editMode")

        toolbar.addMenuProvider(menuProvider)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        if (editMode) {
            try {
                tokensViewModel.tokenToEdit?.let {
                    toolbar.title = "Update Details"

                    issuerField.editText?.setText(it.issuer)
                    labelField.editText?.setText(it.label)

                    thumbnailController.setInitials(tokensViewModel.tokenToEdit!!.issuer)
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
                Toast.makeText(context, "Empty URL", Toast.LENGTH_SHORT).show()
            } catch (exception: BadlyFormedURLException) {
                Log.e(TAG, "onCreate: ", exception)
                Toast.makeText(context, "URL is badly formed", Toast.LENGTH_SHORT).show()
            }
            return
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (secretKeyField.editText!!.text.isEmpty()) {
                        navController.popBackStack()
                    } else {
                        hideKeyboard()
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.enter_details_activity_dialog_back_title)
                            .setMessage(R.string.enter_details_activity_dialog_back_message)
                            .setPositiveButton(R.string.dialog_go_back) { _, _ -> navController.popBackStack() }
                            .setNegativeButton(R.string.dialog_cancel, null)
                            .create()
                            .show()
                    }
                }
            }
        )

        issuerField.editText?.addTextChangedListener(textWatcher)
        labelField.editText?.addTextChangedListener(textWatcher)
        secretKeyField.editText?.addTextChangedListener(textWatcher)
        periodField.editText?.addTextChangedListener(textWatcher)

        saveButton.isEnabled = false
        periodField.editText?.imeOptions = EditorInfo.IME_ACTION_DONE
        secretKeyField.editText?.imeOptions = EditorInfo.IME_ACTION_DONE

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
                    binding.root.findViewById<Button>(algorithmToggleGroup.checkedButtonId).text.toString()

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
                Toast.makeText(context, R.string.error_invalid_chars, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateEntryInDB(token: TokenEntry) {
        addEntryInDB(token, token.id)
    }

    private fun addEntryInDB(token: TokenEntry, oldId: String? = null) {
        if (oldId != null) {
            val isPresent = tokensViewModel.findToken(oldId)
            if (isPresent && otpAuthUrl == null) tokensViewModel.deleteToken(oldId)
        }
        tokensViewModel.addToken(
            token = token,
            requestCode = currentCode,
            onComplete = { responseCode ->
                if (currentCode == responseCode &&
                    navController.currentDestination?.id == R.id.token_details_fragment
                ) {
                    navController.popBackStack()
                }
            },
            onTokenExists = { responseCode ->
                if (currentCode == responseCode &&
                    navController.currentDestination?.id == R.id.token_details_fragment
                ) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Account already exists")
                        .setMessage("You already have a account from '${token.issuer}'")
                        .setPositiveButton("Replace") { _, _ ->
                            tokensViewModel.updateToken(token)
                        }
                        .setNegativeButton("Rename") { _, _ ->
                            saveButton.isEnabled = false
                            issuerField.editText?.requestFocus()
                            issuerField.editText?.setSelection(token.issuer.length)
                            issuerField.editText?.showKeyboard(requireContext(), true)
                        }
                        .create()
                        .show()
                }
            }
        )
    }

    private fun hideKeyboard() {
        issuerField.editText!!.hideKeyboard(requireContext())
        labelField.editText!!.hideKeyboard(requireContext())
        secretKeyField.editText!!.hideKeyboard(requireContext())
        periodField.editText!!.hideKeyboard(requireContext())
    }

    private fun showAdvancedOptions(show: Boolean) {
        if (show) {
            advancedLayoutView.expand(50)
        } else {
            advancedLayoutView.collapse(50) {
                resetAdvanceFields()
            }
        }
    }

    private fun resetAdvanceFields() {
        periodField.editText?.setText("${Constants.DEFAULT_PERIOD}")
        digitsField.editText?.setText("${Constants.DEFAULT_DIGITS}")
        algorithmToggleGroup.check(R.id.btn_sha1)
    }

    private fun deleteToken(entry: TokenEntry) {
        val titleViewBinding = DialogTitleDeleteWarningBinding.inflate(LayoutInflater.from(context))

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

        MaterialAlertDialogBuilder(requireContext())
            .setCustomTitle(titleViewBinding.root)
            .setMessage(R.string.dialog_message_delete_token)
            .setPositiveButton(R.string.dialog_remove) { _, _ ->
                tokensViewModel.deleteToken(entry.id)
                navController.popBackStack()
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

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            if (tokensViewModel.tokenToEdit != null) {
                menuInflater.inflate(R.menu.menu_token, menu)
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.menu_token_delete -> {
                    tokensViewModel.tokenToEdit?.let {
                        deleteToken(it)
                    }
                }
            }
            return true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tokensViewModel.tokenToEdit = null
    }

    companion object {
        private const val TAG = "TokenEntryFragment"
    }
}