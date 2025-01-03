package com.ps.tokky.di

import com.ps.tokky.data.database.TokensDatabase
import com.ps.tokky.data.preferences.PreferenceStore
import com.ps.tokky.data.preferences.SharedPreferenceStore
import com.ps.tokky.data.repositories.TokensRepository
import com.ps.tokky.domain.usecases.DeleteTokenUseCase
import com.ps.tokky.domain.usecases.FetchTokenByIdUseCase
import com.ps.tokky.domain.usecases.FetchTokenByNameUseCase
import com.ps.tokky.domain.usecases.FetchTokensUseCase
import com.ps.tokky.domain.usecases.InsertTokenUseCase
import com.ps.tokky.domain.usecases.InsertTokensUseCase
import com.ps.tokky.domain.usecases.ReplaceExistingTokenUseCase
import com.ps.tokky.helpers.AppSettings
import com.ps.tokky.helpers.BiometricsHelper
import com.ps.tokky.helpers.TokenFormValidator
import com.ps.tokky.ui.viewmodels.AuthenticationViewModel
import com.ps.tokky.ui.viewmodels.ExportTokensViewModel
import com.ps.tokky.ui.viewmodels.HomeViewModel
import com.ps.tokky.ui.viewmodels.ImportTokensViewModel
import com.ps.tokky.ui.viewmodels.RemovePasswordDialogViewModel
import com.ps.tokky.ui.viewmodels.SetPasswordDialogViewModel
import com.ps.tokky.ui.viewmodels.SettingsViewModel
import com.ps.tokky.ui.viewmodels.TokenSetupViewModel
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
