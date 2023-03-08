package com.ps.tokky.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ps.tokky.R
import com.ps.tokky.databinding.ActivitySettingsBinding
import com.ps.tokky.fragments.PreferenceFragment
import com.ps.tokky.utils.AppPreferences

class SettingsActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    private val preferences by lazy { AppPreferences.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (!preferences.allowScreenshots) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }

        setSupportActionBar(binding.collapsingToolbar.toolbar)

        binding.collapsingToolbar.setTitle(R.string.title_settings)
        binding.collapsingToolbar.toolbar.setNavigationIcon(R.drawable.ic_chevron_left)

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