package com.ps.tokky.di

import android.content.Context
import com.ps.tokky.data.preferences.PreferenceStore
import com.ps.tokky.data.preferences.SharedPreferenceStore
import com.ps.tokky.helpers.AppSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideAppPreferenceStore(@ApplicationContext context: Context): PreferenceStore {
        return SharedPreferenceStore(context)
    }

    @Provides
    @Singleton
    fun provideAppSettings(preferences: PreferenceStore): AppSettings {
        return AppSettings(preferences)
    }
}