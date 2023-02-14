package com.ps.tokky.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ps.tokky.databinding.ActivityEnterKeyDetailsBinding

class EnterKeyDetailsActivity : AppCompatActivity() {

    private val binding: ActivityEnterKeyDetailsBinding by lazy {
        ActivityEnterKeyDetailsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}