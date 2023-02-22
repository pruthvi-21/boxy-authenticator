package com.ps.tokky

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ps.tokky.database.DBHelper
import com.ps.tokky.databinding.ActivityMainBinding
import com.ps.tokky.ui.EnterKeyDetailsActivity
import com.ps.tokky.utils.TokenAdapter

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val helper = DBHelper(this)
    private val adapter: TokenAdapter by lazy {
        TokenAdapter(this, binding.recyclerView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))

        binding.fabAddNew.setOnClickListener {
            startActivity(Intent(this, EnterKeyDetailsActivity::class.java))
        }

        binding.refresh.setOnClickListener {
            refresh()
        }
        refresh()
    }

    private fun refresh() {
        val list = helper.getAllEntries()
        adapter.updateEntries(list)
    }

    override fun onResume() {
        super.onResume()
        adapter.onResume()
    }

    override fun onPause() {
        super.onPause()
        adapter.onPause()
    }
}