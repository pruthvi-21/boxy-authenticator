package com.ps.tokky.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ps.tokky.R
import com.ps.tokky.databinding.ActivityMainBinding
import com.ps.tokky.utils.Constants.DELETE_SUCCESS_RESULT_CODE
import com.ps.tokky.utils.DividerItemDecorator
import com.ps.tokky.utils.TokenAdapter
import com.ps.tokky.utils.Utils
import com.ps.tokky.utils.changeOverflowIconColor

class MainActivity : BaseActivity(), View.OnClickListener {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val adapter: TokenAdapter by lazy { TokenAdapter(this, binding.recyclerView, addNewActivityLauncher) }

    private var inEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.toolbar.changeOverflowIconColor(
            Utils.getColorFromAttr(
                this,
                com.google.android.material.R.attr.colorPrimary,
                Color.WHITE
            )
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            val divider =
                DividerItemDecorator(ResourcesCompat.getDrawable(resources, R.drawable.divider, null))
            addItemDecoration(divider)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        binding.expandableFab.fabManual.setOnClickListener(this)
        binding.expandableFab.fabQr.setOnClickListener(this)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.expandableFab.isFabExpanded) {
                    binding.expandableFab.isFabExpanded = false
                    return
                }
                if (!adapter.editModeEnabled) {
                    finish()
                    return
                }
                openEditMode(false)
            }
        })
        refresh(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        menu?.findItem(R.id.menu_main_edit)?.isEnabled = db.getAll(false).isEmpty() || !inEditMode
        menu?.findItem(R.id.menu_main_settings)?.isEnabled = !inEditMode

        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.expandableFab.fabManual.id -> {
                binding.expandableFab.isFabExpanded = false
                addNewActivityLauncher.launch(Intent(this, EnterKeyDetailsActivity::class.java))
            }

            binding.expandableFab.fabQr.id -> {
                binding.expandableFab.isFabExpanded = false
                addNewActivityLauncher.launch(Intent(this, CameraScannerActivity::class.java))
            }
        }
    }

    fun refresh(reload: Boolean) {
        val list = db.getAll(reload)
        adapter.updateEntries(list)
        binding.emptyLayout.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        invalidateOptionsMenu()
    }

    override fun onResume() {
        super.onResume()
        adapter.onResume()
    }

    override fun onPause() {
        super.onPause()
        adapter.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                openEditMode(false)
                return true
            }

            R.id.menu_main_refresh -> {
                refresh(true)
            }

            R.id.menu_main_edit -> {
                openEditMode(!adapter.editModeEnabled)
            }

            R.id.menu_main_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun openEditMode(open: Boolean) {
        inEditMode = open
        if (open) {
            binding.toolbar.title = getString(R.string.edit_mode_title)
            binding.toolbar.navigationIcon =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_close, theme)
            adapter.editModeEnabled = true
        } else {
            binding.toolbar.title = getString(R.string.app_name)
            binding.toolbar.navigationIcon = null
            adapter.editModeEnabled = false

            binding.emptyLayout.visibility =
                if (db.getAll(false).isEmpty()) View.VISIBLE else View.GONE
        }

        if (!open) binding.expandableFab.fabAddNew.show()
        else binding.expandableFab.fabAddNew.hide()

        invalidateOptionsMenu()
    }

    private val addNewActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val extras = it.data?.extras
            if (it.resultCode == Activity.RESULT_OK && extras != null) {
                refresh(true)
                openEditMode(false)
            }
            if (it.resultCode == DELETE_SUCCESS_RESULT_CODE) {
                refresh(true)
                openEditMode(false)
            }
        }

    companion object {
        private const val TAG = "MainActivity"
    }
}