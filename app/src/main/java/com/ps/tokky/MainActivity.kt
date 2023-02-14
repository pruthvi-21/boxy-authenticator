package com.ps.tokky

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ps.tokky.databinding.ActivityMainBinding
import com.ps.tokky.ui.EnterKeyDetailsActivity

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.fabAddNew.setOnClickListener {
            startActivity(Intent(this, EnterKeyDetailsActivity::class.java))
        }
    }
}