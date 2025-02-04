package com.boxy.authenticator.di

import com.boxy.authenticator.data.database.DatabaseDriverFactory
import com.boxy.authenticator.data.preferences.PreferenceStore
import com.boxy.authenticator.data.preferences.SharedPreferenceStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single<PreferenceStore> { SharedPreferenceStore(androidContext()) }
}