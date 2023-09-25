package com.ps.tokky.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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

class MainActivity : BaseActivity(), View.OnClickListener {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val adapter: TokenAdapter by lazy { TokenAdapter(this, binding.recyclerView, addNewActivityLauncher) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Block screenshots
        if (!preferences.allowScreenshots) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

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
        binding.refresh.setOnClickListener(this)
        binding.edit.setOnClickListener(this)
        binding.settings.setOnClickListener(this)

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

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.refresh.id -> {
                refresh(true)
            }

            binding.edit.id -> {
                openEditMode(!adapter.editModeEnabled)
            }

            binding.settings.id -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }

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
        binding.edit.isEnabled = list.isNotEmpty()
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
        }
        return super.onOptionsItemSelected(item)
    }

    fun openEditMode(open: Boolean) {
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

        binding.edit.isEnabled = !open
        binding.settings.isEnabled = !open
        binding.expandableFab.fabAddNew.isEnabled = !open
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