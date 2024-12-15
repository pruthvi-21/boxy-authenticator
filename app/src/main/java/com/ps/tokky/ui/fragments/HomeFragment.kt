package com.ps.tokky.ui.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ps.camerax.BarcodeScanningActivity
import com.ps.camerax.BarcodeScanningActivity.Companion.SCAN_RESULT
import com.ps.tokky.R
import com.ps.tokky.ui.activities.SettingsActivity
import com.ps.tokky.databinding.FragmentHomeBinding
import com.ps.tokky.utils.Constants
import com.ps.tokky.utils.TokenAdapter
import com.ps.tokky.utils.Utils
import com.ps.tokky.utils.changeOverflowIconColor
import com.ps.tokky.utils.toast
import com.ps.tokky.ui.viewmodels.TokensViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentHomeBinding
    private val adapter: TokenAdapter by lazy {
        TokenAdapter(
            requireContext(),
            tokensViewModel,
            navController
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = requireContext()
        val activity = requireActivity()

        tokensViewModel.fetchTokens()

        binding.toolbar.changeOverflowIconColor(
            Utils.getColorFromAttr(
                context,
                com.google.android.material.R.attr.colorPrimary,
                Color.WHITE
            )
        )

        tokensViewModel.tokensState.observe(activity) { uiState ->
            when (uiState) {
                is TokensViewModel.UIState.Success -> {
                    adapter.updateEntries(uiState.data)
                    binding.emptyLayout.visibility = if (uiState.data.isEmpty()) View.VISIBLE else View.GONE
                }

                is TokensViewModel.UIState.Error -> {

                }

                is TokensViewModel.UIState.Loading -> {

                }
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HomeFragment.adapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        binding.expandableFab.fabManual.setOnClickListener(this)
        binding.expandableFab.fabQr.setOnClickListener(this)

        activity.onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.expandableFab.isFabExpanded) {
                        binding.expandableFab.isFabExpanded = false
                        return
                    }
                    activity.finish()
                }
            })

        binding.toolbar.addMenuProvider(toolbarMenuProvider)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.expandableFab.fabManual.id -> {
                binding.expandableFab.isFabExpanded = false
                navController.navigate(R.id.action_home_fragment_to_token_details_fragment)
            }

            binding.expandableFab.fabQr.id -> {
                binding.expandableFab.isFabExpanded = false
                addNewQRActivityLauncher.launch(
                    Intent(context, BarcodeScanningActivity::class.java)
                )
            }
        }
    }

    private val toolbarMenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_main, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.menu_main_settings -> {
                    startActivity(Intent(context, SettingsActivity::class.java))
                }
                R.id.menu_main_export_accounts -> {
                    navController.navigate(HomeFragmentDirections.actionHomeToExportAccounts())
                }

                R.id.menu_main_import_accounts -> {
                    val filePickerIntent = Intent().apply {
                        type = Constants.BACKUP_FILE_MIME_TYPE
                        action = Intent.ACTION_GET_CONTENT
                    }

                    readFileLauncher.launch(filePickerIntent)
                }
            }
            return true
        }

    }

    private val addNewQRActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val extras = it.data?.extras
            if (it.resultCode == Activity.RESULT_OK && extras != null) {
                val auth = extras.getString(SCAN_RESULT)
                if (Utils.isValidTOTPAuthURL(auth)) {
                    tokensViewModel.otpAuthUrl = auth
                    navController.navigate(R.id.action_home_fragment_to_token_details_fragment)
                } else {
                    getString(R.string.error_bad_formed_otp_url).toast(context)
                }
            }
        }

    val readFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val filePath = it.data?.data
            if (filePath != null) {
                navController.navigate(
                    HomeFragmentDirections.actionHomeToImportAccounts(filePath.toString())
                )
            }
        }
}
