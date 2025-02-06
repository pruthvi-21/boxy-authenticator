package com.boxy.authenticator.di

import com.boxy.authenticator.data.database.DatabaseDriverFactory
import com.boxy.authenticator.data.database.dao.LocalTokenDao
import com.boxy.authenticator.data.database.repository.LocalTokenRepository
import com.boxy.authenticator.db.TokenDatabase
import com.boxy.authenticator.domain.database.dao.TokenDao
import com.boxy.authenticator.domain.database.repository.TokenRepository
import com.boxy.authenticator.domain.usecases.DeleteTokenUseCase
import com.boxy.authenticator.domain.usecases.FetchTokenByIdUseCase
import com.boxy.authenticator.domain.usecases.FetchTokenByNameUseCase
import com.boxy.authenticator.domain.usecases.FetchTokensUseCase
import com.boxy.authenticator.domain.usecases.InsertTokenUseCase
import com.boxy.authenticator.domain.usecases.InsertTokensUseCase
import com.boxy.authenticator.domain.usecases.ReplaceExistingTokenUseCase
import com.boxy.authenticator.domain.usecases.UpdateTokenUseCase
import com.boxy.authenticator.core.AppSettings
import com.boxy.authenticator.core.TokenFormValidator
import com.boxy.authenticator.domain.usecases.UpdateHotpCounterUseCase
import com.boxy.authenticator.ui.viewmodels.AuthenticationViewModel
import com.boxy.authenticator.ui.viewmodels.HomeViewModel
import com.boxy.authenticator.ui.viewmodels.ImportTokensViewModel
import com.boxy.authenticator.ui.viewmodels.SetPasswordDialogViewModel
import com.boxy.authenticator.ui.viewmodels.SettingsViewModel
import com.boxy.authenticator.ui.viewmodels.TokenSetupViewModel
import com.boxy.authenticator.ui.viewmodels.TransferAccountsViewModel
import com.boxy.authenticator.ui.viewmodels.RemovePasswordDialogViewModel
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
    viewModel { TokenSetupViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { (biometryAuthenticator: BiometryAuthenticator) ->
        SettingsViewModel(get(), biometryAuthenticator)
    }
    viewModel { TransferAccountsViewModel(get()) }
    viewModel { ImportTokensViewModel(get(), get(), get()) }
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
    factory { UpdateTokenUseCase(get()) }
    factory { UpdateHotpCounterUseCase(get()) }

    //Database
    single<TokenDatabase> {
        TokenDatabase(get<DatabaseDriverFactory>().create())
    }
    single<TokenDao> {
        LocalTokenDao(get<TokenDatabase>())
    }
    single<TokenRepository> {
        LocalTokenRepository(get<TokenDao>())
    }

    factory { TokenFormValidator() }

    single { AppSettings(get()) }
}