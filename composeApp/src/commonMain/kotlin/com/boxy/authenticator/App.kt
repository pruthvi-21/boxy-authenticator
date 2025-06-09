package com.boxy.authenticator

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.boxy.authenticator.core.BiometricsHelper
import com.boxy.authenticator.navigation.RootNavigation
import com.boxy.authenticator.ui.theme.BoxyTheme
import com.boxy.authenticator.ui.util.BindScreenshotBlockerEffect
import com.boxy.authenticator.ui.viewmodels.LocalSettingsViewModel
import com.boxy.authenticator.ui.viewmodels.SettingsViewModel
import dev.icerock.moko.biometry.compose.BindBiometryAuthenticatorEffect
import dev.icerock.moko.biometry.compose.rememberBiometryAuthenticatorFactory
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    val biometryFactory = rememberBiometryAuthenticatorFactory()
    val biometryAuthenticator = biometryFactory.createBiometryAuthenticator()
    BindBiometryAuthenticatorEffect(biometryAuthenticator)

    val biometryHelper: BiometricsHelper = koinInject()
    biometryHelper.init(biometryAuthenticator)

    val settingsViewModel: SettingsViewModel = koinViewModel()
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    BindScreenshotBlockerEffect(settingsUiState.settings.isBlockScreenshotsEnabled)

    CompositionLocalProvider(LocalSettingsViewModel provides settingsViewModel) {
        BoxyTheme(theme = settingsUiState.settings.appTheme) {
            Surface {
                RootNavigation()
            }
        }
    }
}