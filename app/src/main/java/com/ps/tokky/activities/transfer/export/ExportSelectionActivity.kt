package com.ps.tokky.activities.transfer.export

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.ps.tokky.R
import com.ps.tokky.activities.BaseActivity
import com.ps.tokky.activities.transfer.export.ExportActivity.Companion.INTENT_EXTRA_KEY_EXPORT_SELECTION
import com.ps.tokky.databinding.ActivityExportSelectionBinding
import com.ps.tokky.databinding.TransferListExportItemBinding
import com.ps.tokky.utils.DividerItemDecorator

class ExportSelectionActivity : BaseActivity() {

    private val binding by lazy { ActivityExportSelectionBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)

        val checkedList = intent.extras?.getStringArray(INTENT_EXTRA_KEY_EXPORT_SELECTION)

        val tokensList = dbHelper.getAllEntries(true)
        val list = tokensList.map {
            var name = it.issuer
            if (it.label.isNotEmpty()) name += " (${it.label})"
            ExportItem(it.id, name, checkedList!!.contains(it.id))
        }

        val adapter = ExportListAdapter(list)
        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(
            DividerItemDecorator(ResourcesCompat.getDrawable(resources, R.drawable.divider, null))
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_export_selection, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
            R.id.menu_export_selection_done -> {
                val checkedList = (binding.rv.adapter as ExportListAdapter).checkedListIds
                if (checkedList.isEmpty()) {
                    Toast.makeText(
                        this,
                        R.string.export_selection_no_item_selected,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return true
                }
                setResult(
                    Activity.RESULT_OK,
                    Intent().putExtra(INTENT_EXTRA_KEY_EXPORT_SELECTION, checkedList.toTypedArray())
                )
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class ExportItem internal constructor(val id: String?, val name: String?, var checked: Boolean)

    inner class ExportListAdapter(
        private val list: List<ExportItem>
    ) : RecyclerView.Adapter<ExportListAdapter.ViewHolder>() {

        val checkedListIds = ArrayList<String?>()

        init {
            checkedListIds.addAll(list.filter { it.checked }.map { it.id })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = TransferListExportItemBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.binding.checkBox.apply {
                text = item.name
                isChecked = item.checked
            }

            holder.binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && !checkedListIds.contains(item.id)) {
                    checkedListIds.add(item.id)
                }
                if (!isChecked) {
                    checkedListIds.remove(item.id)
                }
            }
        }

        override fun getItemCount() = list.size

        inner class ViewHolder(val binding: TransferListExportItemBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    companion object {
        private const val TAG = "ExportSelectionActivity"
    }
}