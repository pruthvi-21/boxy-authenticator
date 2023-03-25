package com.ps.tokky.activities.transfer.export

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.ps.tokky.R
import com.ps.tokky.activities.BaseActivity
import com.ps.tokky.databinding.ActivityExportBinding
import org.json.JSONArray

class ExportActivity : BaseActivity() {

    private val binding by lazy { ActivityExportBinding.inflate(layoutInflater) }

    var selectedListIds: Array<String?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)

        val exportAccountsList = dbHelper.getAllEntries(false)
        val listIds = exportAccountsList.map { it.id }
        selectedListIds = listIds.toTypedArray()

        binding.summaryCount.text = getString(R.string.export_selection_label_accounts_count, selectedListIds?.size ?: 0)

        binding.editExportListBtn.setOnClickListener {
            selectionActivityLauncher.launch(
                Intent(this, ExportSelectionActivity::class.java)
                    .putExtra(INTENT_EXTRA_KEY_EXPORT_SELECTION, selectedListIds)
            )
        }

        binding.exportFile.setOnClickListener {
            fileExportLauncher.launch(
                Intent(this, ExportFileActivity::class.java)
                    .putExtra(INTENT_EXTRA_KEY_EXPORT_SELECTION, selectedListIds)
            )
        }

        binding.exportQrCode.setOnClickListener {
            val list = selectedListIds?.map { exportAccountsList.find { it1 -> it == it1.id }?.toJson() }
            startActivity(
                Intent(this, ExportBarcodeActivity::class.java)
                    .putExtra(INTENT_EXTRA_KEY_EXPORT_SELECTION, JSONArray(list).toString())
            )
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private val selectionActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val extras = it.data?.extras
            if (it.resultCode == Activity.RESULT_OK && extras != null) {
                selectedListIds = extras.getStringArray(INTENT_EXTRA_KEY_EXPORT_SELECTION)

                binding.exportListTitle.setTitle(
                    if (selectedListIds?.size == dbHelper.getAllEntries(false).size)
                        R.string.export_selection_label_all_accounts
                    else R.string.export_selection_label_selected_accounts
                )
                binding.summaryCount.text = getString(
                    R.string.export_selection_label_accounts_count, selectedListIds?.size ?: 0
                )
            }
        }

    private val fileExportLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_CODE_FILE_CREATION_DONE) {
                Toast.makeText(this, R.string.export_toast_success, Toast.LENGTH_SHORT).show()
                Log.i(TAG, "FileTransferActivityResult: Accounts exported")
            }

            if (it.resultCode == RESULT_CODE_FILE_CREATION_FAILED) {
                Toast.makeText(this, R.string.export_toast_error, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "FileTransferActivityResult: Accounts exported failed")
            }
        }

    companion object {
        private const val TAG = "ExportActivity"
        const val INTENT_EXTRA_KEY_EXPORT_SELECTION = "export_id_list"

        const val RESULT_CODE_FILE_CREATION_DONE = 11212
        const val RESULT_CODE_FILE_CREATION_FAILED = 11213
    }
}