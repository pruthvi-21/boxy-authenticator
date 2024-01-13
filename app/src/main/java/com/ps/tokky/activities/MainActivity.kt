package com.ps.tokky.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ps.camerax.BarcodeScanningActivity
import com.ps.camerax.BarcodeScanningActivity.Companion.SCAN_RESULT
import com.ps.tokky.R
import com.ps.tokky.databinding.ActivityMainBinding
import com.ps.tokky.utils.Constants.DELETE_SUCCESS_RESULT_CODE
import com.ps.tokky.utils.TokenAdapter
import com.ps.tokky.utils.Utils
import com.ps.tokky.utils.changeOverflowIconColor
import com.ps.tokky.utils.toast

class MainActivity : BaseActivity(), View.OnClickListener {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val adapter: TokenAdapter by lazy { TokenAdapter(this, addNewActivityLauncher) }

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
                finish()
            }
        })
        refresh(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        menu?.findItem(R.id.menu_main_refresh)?.isEnabled = false
        Handler(mainLooper).postDelayed({
            menu?.findItem(R.id.menu_main_refresh)?.isEnabled = true
        }, 2000)

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
                addNewQRActivityLauncher.launch(
                    Intent(this, BarcodeScanningActivity::class.java)
                )
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
            R.id.menu_main_refresh -> {
                recreate()
            }

            R.id.menu_main_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val addNewActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val extras = it.data?.extras
            if (it.resultCode == Activity.RESULT_OK && extras != null) {
                refresh(true)
            }
            if (it.resultCode == DELETE_SUCCESS_RESULT_CODE) {
                refresh(true)
            }
        }

    private val addNewQRActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val extras = it.data?.extras
            if (it.resultCode == Activity.RESULT_OK && extras != null) {
                val auth = extras.getString(SCAN_RESULT)
                if (Utils.isValidTOTPAuthURL(auth)) {
                    startActivity(Intent(this, EnterKeyDetailsActivity::class.java).apply {
                        putExtra("otpAuth", auth)
                    })
                } else {
                    getString(R.string.error_bad_formed_otp_url).toast(this)
                }
            }
        }

    companion object {
        private const val TAG = "MainActivity"
    }
}