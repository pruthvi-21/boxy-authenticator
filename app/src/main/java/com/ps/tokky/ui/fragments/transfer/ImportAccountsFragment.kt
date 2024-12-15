package com.ps.tokky.ui.fragments.transfer

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ps.tokky.R
import com.ps.tokky.databinding.DialogImportEditTokenBinding
import com.ps.tokky.databinding.DialogImportPasswordFieldBinding
import com.ps.tokky.databinding.FragmentImportAccountsBinding
import com.ps.tokky.databinding.ItemTransferListImportBinding
import com.ps.tokky.ui.fragments.BaseFragment
import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.utils.FileHelper
import com.ps.tokky.utils.TextWatcherAdapter
import com.ps.tokky.utils.TokenBuilder
import com.ps.tokky.utils.isJsonArray
import com.ps.tokky.utils.toast
import com.ps.tokky.ui.viewmodels.TransferAccountsViewModel
import org.json.JSONArray

class ImportAccountsFragment : BaseFragment() {
    private val args: ImportAccountsFragmentArgs by navArgs()

    private lateinit var binding: FragmentImportAccountsBinding
    private val importList = ArrayList<ImportItem>()

    private val transferAccountsViewModel: TransferAccountsViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentImportAccountsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupPasswordDialog()

        transferAccountsViewModel.importAccountsState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is TransferAccountsViewModel.UIState.Loading -> {}
                is TransferAccountsViewModel.UIState.Success -> {

                    val importedLength = uiState.insertedAccounts.size
                    getString(
                        if (importedLength > 1) R.string.import_success_msg_plural
                        else R.string.import_success_msg_singular,
                        "$importedLength"
                    ).toast(requireContext())

                    if (uiState.duplicateAccounts.isNotEmpty()) {
                        val failedJsonArray = JSONArray()
                        uiState.duplicateAccounts.forEach { failedJsonArray.put(it.toExportJson()) }
                        navController.navigate(
                            ImportAccountsFragmentDirections.actionImportAccountsToImportedDuplicates(
                                failedJsonArray.toString()
                            )
                        )
                    } else {
                        navController.popBackStack()
                    }
                }
            }
        }
    }

    private fun updateItem(item: ImportItem, isChecked: Boolean) {
        item.checked = isChecked
        binding.btnImport.isEnabled = !importList.none { it.checked }
    }

    private fun setupPasswordDialog() {
        val dialogBinding = DialogImportPasswordFieldBinding.inflate(LayoutInflater.from(requireContext()))

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.import_password_dialog_title)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.import_password_dialog_positive_btn) { _, _ ->
                val filePath = Uri.parse(args.filePath)
                val password = dialogBinding.tilPassword.editText?.text.toString()
                val fileData = FileHelper.readFromFile(requireContext(), filePath, password)

                if (!fileData.isJsonArray()) {
                    Toast.makeText(requireContext(), R.string.import_password_failed_msg, Toast.LENGTH_SHORT)
                        .show()
                    return@setPositiveButton
                }

                val importJsonArray = JSONArray(fileData)

                importList.clear()
                for (i in 0 until importJsonArray.length()) {
                    val jsonObj = importJsonArray.getJSONObject(i)
                    val token = TokenBuilder.buildFromExportJson(jsonObj)

                    importList.add(ImportItem(token, true))
                }

                val adapter = ImportListAdapter()
                binding.rv.adapter = adapter

                binding.btnImport.setOnClickListener {
                    val checkedAccounts = importList.filter { it.checked }.map { it.token }
                    Log.e(TAG, "checkedAccounts: ${checkedAccounts.size}")
                    transferAccountsViewModel.importAccounts(checkedAccounts)
                }
            }
            .setNegativeButton(R.string.import_password_dialog_negative_btn) { _, _ -> navController.popBackStack() }
            .show()
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

                val dialog = MaterialAlertDialogBuilder(requireContext())
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
    }
}