package com.ps.tokky.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ps.tokky.ui.screens.HomeScreen
import com.ps.tokky.ui.theme.TokkyTheme
import com.ps.tokky.ui.viewmodels.TokensViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val tokensViewModel: TokensViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        setContent {
            TokkyTheme {

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "home",
                ) {
                    composable("home") {
                        HomeScreen(
                            tokensViewModel = tokensViewModel,
                            navController = navController,
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}