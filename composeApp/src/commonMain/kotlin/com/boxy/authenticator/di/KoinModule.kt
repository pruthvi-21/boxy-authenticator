package com.boxy.authenticator.di

import com.boxy.authenticator.data.database.TokensDao
import com.boxy.authenticator.data.database.TokensDatabase
import com.boxy.authenticator.data.repositories.TokensRepository
import com.boxy.authenticator.domain.usecases.DeleteTokenUseCase
import com.boxy.authenticator.domain.usecases.FetchTokenByIdUseCase
import com.boxy.authenticator.domain.usecases.FetchTokenByNameUseCase
import com.boxy.authenticator.domain.usecases.FetchTokensUseCase
import com.boxy.authenticator.domain.usecases.InsertTokenUseCase
import com.boxy.authenticator.domain.usecases.InsertTokensUseCase
import com.boxy.authenticator.domain.usecases.ReplaceExistingTokenUseCase
import com.boxy.authenticator.helpers.AppSettings
import com.boxy.authenticator.helpers.TokenFormValidator
import com.boxy.authenticator.ui.viewmodels.AuthenticationViewModel
import com.boxy.authenticator.ui.viewmodels.HomeViewModel
import com.boxy.authenticator.ui.viewmodels.SetPasswordDialogViewModel
import com.boxy.authenticator.ui.viewmodels.SettingsViewModel
import com.boxy.authenticator.ui.viewmodels.TokenSetupViewModel
import com.ps.tokky.ui.viewmodels.RemovePasswordDialogViewModel
import dev.icerock.moko.biometry.BiometryAuthenticator
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    viewModel { (biometryAuthenticator: BiometryAuthenticator) ->
        AuthenticationViewModel(get(), biometryAuthenticator)
    }
    viewModel { HomeViewModel(get()) }
    viewModel { TokenSetupViewModel(get(), get(), get(), get()) }
    viewModel { (biometryAuthenticator: BiometryAuthenticator) ->
        SettingsViewModel(get(), biometryAuthenticator)
    }
    viewModel { RemovePasswordDialogViewModel() }
    viewModel { SetPasswordDialogViewModel() }

    // UseCases
    factory { DeleteTokenUseCase(get()) }
    factory { FetchTokenByIdUseCase(get()) }
    factory { FetchTokenByNameUseCase(get()) }
    factory { FetchTokensUseCase(get()) }
    factory { InsertTokenUseCase(get()) }
    factory { InsertTokensUseCase(get()) }
    factory { ReplaceExistingTokenUseCase(get()) }

    single<TokensDatabase> { TokensDatabase.build(get()) }
    single<TokensDao> { get<TokensDatabase>().getTokensDao() }
    single { TokensRepository(get()) }

    factory { TokenFormValidator() }

    single { AppSettings(get()) }
}