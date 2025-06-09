package com.boxy.authenticator

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.boxy.authenticator.navigation.RootNavigation
import com.boxy.authenticator.ui.theme.BoxyTheme
import com.boxy.authenticator.ui.util.BindScreenshotBlockerEffect
import com.boxy.authenticator.ui.viewmodels.LocalSettingsViewModel
import com.boxy.authenticator.ui.viewmodels.SettingsViewModel
import dev.icerock.moko.biometry.compose.BindBiometryAuthenticatorEffect
import dev.icerock.moko.biometry.compose.rememberBiometryAuthenticatorFactory
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.ParametersHolder

@Composable
fun App() {
    val biometryFactory = rememberBiometryAuthenticatorFactory()
    val biometryAuthenticator = biometryFactory.createBiometryAuthenticator()
    val settingsViewModel: SettingsViewModel = koinViewModel {
        ParametersHolder(mutableListOf(biometryAuthenticator))
    }

    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    BindBiometryAuthenticatorEffect(biometryAuthenticator)
    BindScreenshotBlockerEffect(settingsUiState.settings.isBlockScreenshotsEnabled)

    KoinContext {
        CompositionLocalProvider(LocalSettingsViewModel provides settingsViewModel) {
            BoxyTheme(theme = settingsUiState.settings.appTheme) {
                Surface {
                    RootNavigation()
                }
            }
        }
    }
}