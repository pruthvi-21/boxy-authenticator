package com.ps.tokky.activities.transfer.imports

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ps.tokky.R
import com.ps.tokky.activities.BaseActivity
import com.ps.tokky.databinding.ActivityImportBinding
import com.ps.tokky.databinding.DialogImportEditTokenBinding
import com.ps.tokky.databinding.ItemTransferListImportBinding
import com.ps.tokky.models.TokenEntry
import com.ps.tokky.utils.*
import org.json.JSONArray

class ImportActivity : BaseActivity() {

    private val binding by lazy { ActivityImportBinding.inflate(layoutInflater) }
    private val importList = ArrayList<ImportItem>()

    private val failedList = ArrayList<TokenEntry>()

    private val context by lazy { this }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)

        val filePath = Uri.parse(intent.extras?.getString(INTENT_EXTRA_KEY_FILE_PATH))

        val jsonArray = FileHelper.readFromFile(context, filePath)

        importList.clear()
        for (i in 0 until jsonArray.length()) {
            val jsonObj = jsonArray.getJSONObject(i)
            val token = TokenEntry.BuildFromExportJson(this, jsonObj).build()

            importList.add(ImportItem(token, true))
        }

        val adapter = ImportListAdapter()
        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(
            DividerItemDecorator(ResourcesCompat.getDrawable(resources, R.drawable.divider, null))
        )

        binding.btnImport.setOnClickListener {
            val checkedList = importList.filter { it.checked }
            checkedList.forEach {
                val token = it.token
                try {
                    db.add(token)
                } catch (exception: TokenExistsInDBException) {
                    failedList.add(token)
                }
            }

            val importedLength = checkedList.size - failedList.size
            getString(
                if (importedLength > 1) R.string.import_success_msg_plural
                else R.string.import_success_msg_singular,
                "$importedLength"
            ).toast(context)

            if (failedList.isNotEmpty()) {
                val failedJsonArray = JSONArray()
                failedList.forEach { failedJsonArray.put(it.toExportJson()) }
                val intent = Intent(context, ImportFailedActivity::class.java)
                    .putExtra(INTENT_EXTRA_FAILED_LIST, failedJsonArray.toString())

                startActivity(intent)
            }
            finish()
        }
    }

    private fun updateItem(item: ImportItem, isChecked: Boolean) {
        item.checked = isChecked
        binding.btnImport.isEnabled = !importList.none { it.checked }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    class ImportItem internal constructor(val token: TokenEntry, var checked: Boolean)

    inner class ImportListAdapter : RecyclerView.Adapter<ImportListAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemTransferListImportBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = importList[position]
            holder.binding.checkBox.apply {
                text = item.token.name
                isChecked = item.checked
            }

            holder.binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                updateItem(item, isChecked)
            }

            holder.binding.edit.setOnClickListener {
                val updateView = DialogImportEditTokenBinding.inflate(layoutInflater)

                updateView.tilIssuer.editText!!.setText(item.token.issuer)
                updateView.tilLabel.editText!!.setText(item.token.label)

                updateView.tilIssuer.editText?.addTextChangedListener(object :
                    TextWatcherAdapter() {
                    override fun afterTextChanged(editable: Editable) {
                        updateView.errorHolder.visibility = View.GONE
                    }
                })

                val dialog = MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.import_edit_dialog_title)
                    .setView(updateView.root)
                    .setPositiveButton(R.string.import_edit_dialog_positive_btn, null)
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .create()
                dialog.show()

                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    val newIssuer = updateView.tilIssuer.editText!!.text.toString()
                    val newLabel = updateView.tilLabel.editText!!.text.toString()

                    if (newIssuer.isEmpty()) {
                        updateView.errorHolder.visibility = View.VISIBLE
                        updateView.errorHolder.setText(R.string.import_edit_dialog_empty_error_msg)
                    } else {
                        importList[position].token.updateInfo(newIssuer, newLabel)
                        notifyItemChanged(position)
                        dialog.dismiss()
                    }
                }
            }
        }

        override fun getItemCount() = importList.size

        inner class ViewHolder(val binding: ItemTransferListImportBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    companion object {
        private const val TAG = "ImportActivity"

        const val INTENT_EXTRA_KEY_FILE_PATH = "file_path"
        const val INTENT_EXTRA_FAILED_LIST = "file_path"
    }
}