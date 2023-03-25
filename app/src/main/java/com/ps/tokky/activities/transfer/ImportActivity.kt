package com.ps.tokky.activities.transfer

import android.os.Bundle
import android.view.MenuItem
import com.ps.tokky.activities.BaseActivity
import com.ps.tokky.databinding.ActivityTokensImportBinding

class ImportActivity : BaseActivity() {

    private val binding by lazy { ActivityTokensImportBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "ImportActivity"
    }
}