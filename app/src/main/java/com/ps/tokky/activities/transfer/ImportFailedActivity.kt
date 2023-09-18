package com.ps.tokky.activities.transfer

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ps.tokky.R
import com.ps.tokky.activities.BaseActivity
import com.ps.tokky.activities.transfer.ImportActivity.Companion.INTENT_EXTRA_FAILED_LIST
import com.ps.tokky.databinding.ActivityImportFailedBinding
import com.ps.tokky.databinding.DialogImportEditTokenBinding
import com.ps.tokky.databinding.ItemTransferListImportFailedBinding
import com.ps.tokky.models.TokenEntry
import com.ps.tokky.utils.TextWatcherAdapter
import com.ps.tokky.utils.TokenExistsInDBException
import com.ps.tokky.utils.toast
import org.json.JSONArray

class ImportFailedActivity : BaseActivity() {

    private val binding by lazy { ActivityImportFailedBinding.inflate(layoutInflater) }
    val importList = ArrayList<TokenEntry>()

    private val context by lazy { this }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val list = intent.extras?.getString(INTENT_EXTRA_FAILED_LIST) ?: return
        val jsonArray = JSONArray(list)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            importList.add(TokenEntry.BuildFromExportJson(this, obj).build())
        }

        binding.rv.adapter = ImportFailedListAdapter()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    inner class ImportFailedListAdapter :
        RecyclerView.Adapter<ImportFailedListAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemTransferListImportFailedBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = importList[position]

            holder.binding.nameLabel.text = item.name

            holder.binding.edit.setOnClickListener {
                editToken(holder, item)
            }

            holder.binding.remove.setOnClickListener {
                importList.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)
                if (importList.isEmpty()) finish()
            }
        }

        override fun getItemCount() = importList.size

        private fun editToken(holder: ViewHolder, item: TokenEntry) {
            val updateView = DialogImportEditTokenBinding.inflate(layoutInflater)

            val currentIssuer = item.issuer
            val currentLabel = item.label

            updateView.tilIssuer.editText!!.setText(item.issuer)
            updateView.tilLabel.editText!!.setText(item.label)

            val textWatcher = object : TextWatcherAdapter() {
                override fun afterTextChanged(editable: Editable) {
                    updateView.errorHolder.visibility = View.GONE
                }
            }

            updateView.tilIssuer.editText?.addTextChangedListener(textWatcher)
            updateView.tilLabel.editText?.addTextChangedListener(textWatcher)

            val dialog = MaterialAlertDialogBuilder(context)
                .setTitle(R.string.import_failed_edit_dialog_title)
                .setView(updateView.root)
                .setPositiveButton(R.string.import_failed_edit_dialog_positive_btn, null)
                .setNegativeButton(R.string.dialog_cancel) { _, _ ->
                    item.updateInfo(currentIssuer, currentLabel)
                }
                .create()
            dialog.show()

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val newIssuer = updateView.tilIssuer.editText!!.text.toString()
                val newLabel = updateView.tilLabel.editText!!.text.toString()

                if (newIssuer.isEmpty()) {
                    getString(R.string.import_edit_dialog_empty_error_msg).toast(context)
                    return@setOnClickListener
                }

                item.updateInfo(newIssuer, newLabel)

                try {
                    val success = db.add(item)

                    if (success) {
                        importList.remove(item)
                        notifyItemRemoved(holder.adapterPosition)
                        notifyItemRangeChanged(holder.adapterPosition, importList.size)
                        dialog.dismiss()
                    } else {
                        item.updateInfo(currentIssuer, currentLabel)
                        getString(R.string.error_db_entry_failed).toast(context)
                    }
                } catch (exception: TokenExistsInDBException) {
                    updateView.errorHolder.visibility = View.VISIBLE
                    updateView.errorHolder.setText(R.string.import_failed_dialog_existing_token)
                }
                if (importList.isEmpty()) finish()
            }
        }

        inner class ViewHolder(val binding: ItemTransferListImportFailedBinding) :
            RecyclerView.ViewHolder(binding.root)
    }
}