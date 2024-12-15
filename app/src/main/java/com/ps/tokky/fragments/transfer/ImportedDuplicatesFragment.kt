package com.ps.tokky.fragments.transfer

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ps.tokky.R
import com.ps.tokky.databinding.DialogImportEditTokenBinding
import com.ps.tokky.databinding.FragmentImportedDuplicatesBinding
import com.ps.tokky.databinding.ItemTransferListImportFailedBinding
import com.ps.tokky.fragments.BaseFragment
import com.ps.tokky.models.TokenEntry
import com.ps.tokky.utils.TextWatcherAdapter
import com.ps.tokky.utils.TokenBuilder
import com.ps.tokky.utils.toast
import org.json.JSONArray

class ImportedDuplicatesFragment : BaseFragment() {
    private val args: ImportedDuplicatesFragmentArgs by navArgs()

    private lateinit var binding: FragmentImportedDuplicatesBinding
    val importList = ArrayList<TokenEntry>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentImportedDuplicatesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val list = args.failedTokensJsonArray
        val jsonArray = JSONArray(list)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            importList.add(TokenBuilder.buildFromExportJson(obj))
        }

        binding.rv.adapter = ImportFailedListAdapter()
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
                if (importList.isEmpty()) navController.popBackStack()
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

            val dialog = MaterialAlertDialogBuilder(requireContext())
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

                val requestCode = (Math.random() * 1000).toString()
                tokensViewModel.addToken(
                    item,
                    requestCode,
                    onComplete = { code ->
                        if (code == requestCode) {
                            importList.remove(item)
                            notifyItemRemoved(holder.adapterPosition)
                            notifyItemRangeChanged(holder.adapterPosition, importList.size)
                            dialog.dismiss()
                        }
                    },
                    onDuplicate = { code, _ ->
                        if (code == requestCode) {
                            updateView.errorHolder.visibility = View.VISIBLE
                            updateView.errorHolder.setText(R.string.import_failed_dialog_existing_token)
                        }
                    }
                )
                if (importList.isEmpty()) navController.popBackStack()
            }
        }

        inner class ViewHolder(val binding: ItemTransferListImportFailedBinding) :
            RecyclerView.ViewHolder(binding.root)
    }
}