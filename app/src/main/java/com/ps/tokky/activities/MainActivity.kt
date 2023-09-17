package com.ps.tokky.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ps.tokky.R
import com.ps.tokky.databinding.ActivityMainBinding
import com.ps.tokky.utils.DividerItemDecorator
import com.ps.tokky.utils.TokenAdapter

class MainActivity : BaseActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val adapter: TokenAdapter by lazy {
        TokenAdapter(this, binding.recyclerView, addNewActivityLauncher)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Block screenshots
        if (!preferences.allowScreenshots) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        val divider =
            DividerItemDecorator(ResourcesCompat.getDrawable(resources, R.drawable.divider, null))
        binding.recyclerView.addItemDecoration(divider)
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        binding.fabAddNew.setOnClickListener {
            addNewActivityLauncher.launch(Intent(this, EnterKeyDetailsActivity::class.java))
        }

        binding.fabAddNew.setOnLongClickListener {
            addNewActivityLauncher.launch(Intent(this, CameraScannerActivity::class.java))
            true
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!adapter.editModeEnabled) {
                    finish()
                    return
                }
                openEditMode(false)
            }
        })
        refresh(true)

        binding.refresh.setOnClickListener {
            refresh(true)
        }

        binding.edit.setOnClickListener {
            openEditMode(!adapter.editModeEnabled)
        }

        binding.settings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
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
        }
        return super.onOptionsItemSelected(item)
    }

    fun openEditMode(open: Boolean) {
        invalidateOptionsMenu()
        if (open) {
            binding.toolbar.title = getString(R.string.edit_mode_title)
            binding.toolbar.navigationIcon =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_close, theme)
            adapter.editModeEnabled = true
            slideDownBottomBar()
        } else {
            binding.toolbar.title = getString(R.string.app_name)
            binding.toolbar.navigationIcon = null
            adapter.editModeEnabled = false
            slideUpBottomBar()

            binding.emptyLayout.visibility =
                if (db.getAll(false).isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun slideDownBottomBar() {
        val animation = TranslateAnimation(0f, 0f, 0f, binding.bottomBar.height.toFloat() - 50)
        animation.duration = 150
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(var1: Animation?) {}
            override fun onAnimationEnd(var1: Animation?) {
                binding.bottomBar.visibility = View.GONE
            }

            override fun onAnimationRepeat(var1: Animation?) {}
        })
        binding.bottomBar.startAnimation(animation)
    }

    private fun slideUpBottomBar() {
        binding.bottomBar.visibility = View.VISIBLE
        val animation = TranslateAnimation(0f, 0f, binding.bottomBar.height.toFloat() - 50, 0f)
        animation.duration = 150
        binding.bottomBar.startAnimation(animation)
    }

    private val addNewActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val extras = it.data?.extras
            if (it.resultCode == Activity.RESULT_OK && extras != null) {
                refresh(true)
            }
        }


    companion object {
        private const val TAG = "MainActivity"
    }
}