package com.ps.tokky.activities

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ps.tokky.R
import com.ps.tokky.databinding.ActivityMainBinding
import com.ps.tokky.utils.AppPreferences
import com.ps.tokky.utils.DBHelper
import com.ps.tokky.utils.DividerItemDecorator
import com.ps.tokky.utils.TokenAdapter
import kotlin.math.roundToLong

class MainActivity : AppCompatActivity() {

    private var userAuthenticated = false
        set(value) {
            field = value
            if (field) refresh(true)
        }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val helper = DBHelper.getInstance(this)
    private val adapter: TokenAdapter by lazy {
        TokenAdapter(this, ArrayList(), binding.recyclerView, addNewActivityLauncher)
    }
    private val preferences by lazy { AppPreferences.getInstance(this) }

    //For biometric authentication
    private val executor by lazy { ContextCompat.getMainExecutor(this) }
    private var biometricPrompt: BiometricPrompt? = null
    private val promptInfo by lazy {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.unlock_prompt_title))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Block screenshots
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        setContentView(binding.root)
        setSupportActionBar(binding.collapsingToolbar.toolbar)

        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                userAuthenticated = true
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Toast.makeText(this@MainActivity, "Unable to verify user identity", Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onAuthenticationFailed() {
                Log.w(TAG, "onAuthenticationFailed: Incorrect attempt")
            }
        })

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        val divider = DividerItemDecorator(ResourcesCompat.getDrawable(resources, R.drawable.divider, null))
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
                }
                openEditMode(false)
            }
        })
    }

    private fun refresh(reload: Boolean) {
        val list = helper.getAllEntries(reload)
        adapter.updateEntries(list)
        binding.emptyLayout.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        invalidateOptionsMenu()
    }

    override fun onResume() {
        super.onResume()
        adapter.onResume()

        if (!preferences.appLockEnabled) userAuthenticated = true

        if (!userAuthenticated) {
            biometricPrompt?.authenticate(promptInfo)
        }
    }

    override fun onPause() {
        super.onPause()
        adapter.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!adapter.editModeEnabled) {
            menuInflater.inflate(R.menu.menu_main, menu)
            menu?.getItem(1)?.isEnabled = helper.getAllEntries(false).isNotEmpty()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                openEditMode(false)
                return true
            }
            R.id.menu_main_refresh -> {
                refresh(true)
                return true
            }
            R.id.menu_main_edit -> {
                openEditMode(true)
                return true
            }
            R.id.menu_main_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun openEditMode(open: Boolean) {

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                duration = (duration * ValueAnimator.getDurationScale()).roundToLong()
            }
        }
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                duration = (duration * ValueAnimator.getDurationScale()).roundToLong()
            }
        }

        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                if (open) {
                    adapter.editModeEnabled = true
                    invalidateOptionsMenu()
                    binding.collapsingToolbar.toolbar.setTitle(R.string.edit)
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
                    binding.fabAddNew.visibility = View.GONE
                } else {
                    adapter.editModeEnabled = false
                    invalidateOptionsMenu()
                    supportActionBar?.setTitle(R.string.app_name)
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    supportActionBar?.setHomeAsUpIndicator(0)
                    binding.collapsingToolbar.toolbar.navigationIcon = null
                    binding.fabAddNew.visibility = View.VISIBLE
//                  refresh(false)
                }
                binding.root.startAnimation(fadeIn)
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

        })
        binding.root.startAnimation(fadeOut)
    }


    private val addNewActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val extras = it.data?.extras
            if (it.resultCode == Activity.RESULT_OK && extras != null) {
                adapter.addToken(helper.getAllEntries(false).find { it1 -> it1.id == extras.getString("id") })
            }
        }

    companion object {
        private const val TAG = "MainActivity"
    }
}