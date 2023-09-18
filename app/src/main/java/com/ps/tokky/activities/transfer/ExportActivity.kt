package com.ps.tokky.activities.transfer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.ps.tokky.R
import com.ps.tokky.activities.BaseActivity
import com.ps.tokky.databinding.ActivityExportBinding
import com.ps.tokky.utils.Constants
import com.ps.tokky.utils.Constants.BACKUP_FILE_MIME_TYPE
import com.ps.tokky.utils.FileHelper
import org.json.JSONArray

class ExportActivity : BaseActivity() {

    private val binding by lazy { ActivityExportBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.tilPassword.editText?.addTextChangedListener(passwordTextWatcher)
        binding.tilConfirmPassword.editText?.addTextChangedListener(passwordTextWatcher)

        binding.export.setOnClickListener {
            val password1 = binding.tilPassword.editText?.text?.toString() ?: ""
            val password2 = binding.tilConfirmPassword.editText?.text?.toString() ?: ""

            if (password1.isEmpty() || password2.isEmpty()) {
                if (password1.isEmpty())
                    binding.tilPassword.error = getString(R.string.export_password_empty)
                if (password2.isEmpty())
                    binding.tilConfirmPassword.error =
                        getString(R.string.export_password_empty)
                return@setOnClickListener
            }
            if (binding.tilPassword.editText?.text?.toString() != binding.tilConfirmPassword.editText?.text?.toString()) {
                binding.tilConfirmPassword.error =
                    getString(R.string.export_password_mismatch)
                return@setOnClickListener
            } else {
                launchDirectoryPicker(Constants.EXPORT_FILE_NAME)
            }
        }
    }

    private fun launchDirectoryPicker(fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = BACKUP_FILE_MIME_TYPE
            putExtra(Intent.EXTRA_TITLE, "$fileName.txt")
        }
        launcher.launch(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val intent = result.data
            if (result.resultCode == Activity.RESULT_OK && intent != null) {
                if (intent.data == null) return@registerForActivityResult

                val exportData = JSONArray(db.getAll(false).map { it.toExportJson() }).toString()
                FileHelper.writeToFile(
                    context = this,
                    uri = intent.data!!,
                    content = exportData,
                    password = binding.tilPassword.editText?.text.toString(),
                    successCallback = {
                        Toast.makeText(this, R.string.export_toast_success, Toast.LENGTH_SHORT)
                            .show()
                        Log.i(TAG, "FileTransferActivityResult: Accounts exported")
                        finish()
                    },
                    failureCallback = {
                        Toast.makeText(this, R.string.export_toast_error, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "FileTransferActivityResult: Accounts exported failed")
                        finish()
                    })
            } else {
                Log.e(TAG, "Some Error Occurred : $result")
            }
        }

    private val passwordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.tilPassword.isErrorEnabled = false
            binding.tilConfirmPassword.isErrorEnabled = false
        }
    }

    companion object {
        private const val TAG = "ExportActivity"
    }
}