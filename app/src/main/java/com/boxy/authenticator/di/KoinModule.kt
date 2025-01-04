package com.boxy.authenticator.di

import com.boxy.authenticator.data.database.TokensDatabase
import com.boxy.authenticator.data.preferences.PreferenceStore
import com.boxy.authenticator.data.preferences.SharedPreferenceStore
import com.boxy.authenticator.data.repositories.TokensRepository
import com.boxy.authenticator.domain.usecases.DeleteTokenUseCase
import com.boxy.authenticator.domain.usecases.FetchTokenByIdUseCase
import com.boxy.authenticator.domain.usecases.FetchTokenByNameUseCase
import com.boxy.authenticator.domain.usecases.FetchTokensUseCase
import com.boxy.authenticator.domain.usecases.InsertTokenUseCase
import com.boxy.authenticator.domain.usecases.InsertTokensUseCase
import com.boxy.authenticator.domain.usecases.ReplaceExistingTokenUseCase
import com.boxy.authenticator.helpers.AppSettings
import com.boxy.authenticator.helpers.BiometricsHelper
import com.boxy.authenticator.helpers.TokenFormValidator
import com.boxy.authenticator.ui.viewmodels.AuthenticationViewModel
import com.boxy.authenticator.ui.viewmodels.ExportTokensViewModel
import com.boxy.authenticator.ui.viewmodels.HomeViewModel
import com.boxy.authenticator.ui.viewmodels.ImportTokensViewModel
import com.boxy.authenticator.ui.viewmodels.RemovePasswordDialogViewModel
import com.boxy.authenticator.ui.viewmodels.SetPasswordDialogViewModel
import com.boxy.authenticator.ui.viewmodels.SettingsViewModel
import com.boxy.authenticator.ui.viewmodels.TokenSetupViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sharedModule = module {
    viewModel { AuthenticationViewModel(get(), get()) }
    viewModel { ExportTokensViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { ImportTokensViewModel(get(), get(), get()) }
    viewModel { RemovePasswordDialogViewModel() }
    viewModel { SetPasswordDialogViewModel() }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { TokenSetupViewModel(get(), get(), get(), get(), get()) }

    single<PreferenceStore> { SharedPreferenceStore(get()) }
    single { AppSettings(get()) }
}

val tokensModule = module {
    // Database
    single { TokensDatabase.getInstance(androidContext()) }
    single { get<TokensDatabase>().tokensDao() }
    single { TokensRepository(get()) }

    // UseCases
    factory { DeleteTokenUseCase(get()) }
    factory { FetchTokenByIdUseCase(get()) }
    factory { FetchTokenByNameUseCase(get()) }
    factory { FetchTokensUseCase(get()) }
    factory { InsertTokenUseCase(get()) }
    factory { InsertTokensUseCase(get()) }
    factory { ReplaceExistingTokenUseCase(get()) }

    // Helpers and Validators
    factory { TokenFormValidator(get()) }
    single { BiometricsHelper() }
}
