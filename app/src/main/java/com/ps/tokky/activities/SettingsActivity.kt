package com.ps.tokky.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import com.ps.tokky.databinding.ActivitySettingsBinding
import com.ps.tokky.fragments.PreferenceFragment

class SettingsActivity : BaseActivity() {

    private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)

        if (!preferences.allowScreenshots) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }

        supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentContainer.id, PreferenceFragment())
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}