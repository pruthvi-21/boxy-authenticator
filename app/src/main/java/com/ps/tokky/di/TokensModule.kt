package com.ps.tokky.di

import android.content.Context
import android.content.res.Resources
import com.ps.tokky.data.database.TokensDao
import com.ps.tokky.data.database.TokensDatabase
import com.ps.tokky.data.repositories.TokensRepository
import com.ps.tokky.helpers.BiometricsHelper
import com.ps.tokky.ui.viewmodels.TokenFormValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TokensModule {

    @Provides
    @Singleton
    fun provideTokensDao(@ApplicationContext context: Context): TokensDao {
        return TokensDatabase.getInstance(context).tokensDao()
    }

    @Provides
    @Singleton
    fun provideTokensRepository(tokensDao: TokensDao): TokensRepository {
        return TokensRepository(tokensDao)
    }

    @Provides
    fun provideResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }

    @Provides
    fun provideTokenFormValidator(resources: Resources): TokenFormValidator {
        return TokenFormValidator(resources)
    }

    @Provides
    @Singleton
    fun provideBiometricsHelper(): BiometricsHelper {
        return BiometricsHelper()
    }
}