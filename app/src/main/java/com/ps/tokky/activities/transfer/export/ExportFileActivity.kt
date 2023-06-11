package com.ps.tokky.activities.transfer.export

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.ps.tokky.R
import com.ps.tokky.activities.BaseActivity
import com.ps.tokky.activities.transfer.export.ExportActivity.Companion.INTENT_EXTRA_KEY_EXPORT_SELECTION
import com.ps.tokky.activities.transfer.export.ExportActivity.Companion.RESULT_CODE_FILE_CREATION_DONE
import com.ps.tokky.activities.transfer.export.ExportActivity.Companion.RESULT_CODE_FILE_CREATION_FAILED
import com.ps.tokky.databinding.ActivityExportFileBinding
import com.ps.tokky.models.TokenEntry
import com.ps.tokky.utils.Constants.EXPORT_FILE_NAME
import com.ps.tokky.utils.Constants.FILE_MIME_TYPE
import com.ps.tokky.utils.FileHelper
import org.json.JSONArray

class ExportFileActivity : BaseActivity() {

    private val binding by lazy { ActivityExportFileBinding.inflate(layoutInflater) }

    private val exportData: List<TokenEntry?>? by lazy {
        val data = intent?.extras?.getStringArray(INTENT_EXTRA_KEY_EXPORT_SELECTION)
        data?.asList()?.let { db.getEntriesWithIDs(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.tilPassword.editText?.addTextChangedListener(textWatcher)
        binding.tilConfirmPassword.editText?.addTextChangedListener(textWatcher)

        binding.exportBtn.setOnClickListener {
            val password1 = binding.tilPassword.editText?.text?.toString() ?: ""
            val password2 = binding.tilConfirmPassword.editText?.text?.toString() ?: ""

            if (password1.isEmpty() || password2.isEmpty()) {
                if (password1.isEmpty())
                    binding.tilPassword.error = getString(R.string.export_file_password_empty)
                if (password2.isEmpty())
                    binding.tilConfirmPassword.error = getString(R.string.export_file_password_empty)
                return@setOnClickListener
            }
            if (binding.tilPassword.editText?.text?.toString() != binding.tilConfirmPassword.editText?.text?.toString()) {
                binding.tilConfirmPassword.error = getString(R.string.export_file_password_mismatch)
                return@setOnClickListener
            }

        }

        launchDirectoryPicker(EXPORT_FILE_NAME)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.tilPassword.isErrorEnabled = false
            binding.tilConfirmPassword.isErrorEnabled = false
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val intent = result.data
        if (result.resultCode == Activity.RESULT_OK && intent != null) {
            if (intent.data == null) return@registerForActivityResult

            val successCallback = {
                setResult(RESULT_CODE_FILE_CREATION_DONE)
                finish()
            }
            val failureCallback = {
                setResult(RESULT_CODE_FILE_CREATION_FAILED)
                finish()
            }

            FileHelper.writeToFile(
                context = this@ExportFileActivity,
                uri = intent.data,
                content = JSONArray(exportData?.map { it?.toExportJson() }).toString(),
                successCallback = successCallback,
                failureCallback = failureCallback
            )
        } else {
            Log.e(TAG, "Some Error Occurred : $result")
        }
    }

    private fun launchDirectoryPicker(fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = FILE_MIME_TYPE
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        launcher.launch(intent)
    }

    companion object {
        const val TAG = "ExportFileActivity"
    }
}