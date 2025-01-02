package com.ps.tokky.di

import android.content.Context
import com.ps.tokky.data.database.TokensDao
import com.ps.tokky.data.database.TokensDatabase
import com.ps.tokky.data.repositories.TokensRepository
import com.ps.tokky.helpers.BiometricsHelper
import com.ps.tokky.helpers.TokenFormValidator
import com.ps.tokky.helpers.TokensManager
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
    @Singleton
    fun provideTokensManager(tokensRepository: TokensRepository): TokensManager {
        return TokensManager(tokensRepository)
    }

    @Provides
    fun provideTokenFormValidator(@ApplicationContext context: Context): TokenFormValidator {
        return TokenFormValidator(context)
    }

    @Provides
    @Singleton
    fun provideBiometricsHelper(): BiometricsHelper {
        return BiometricsHelper()
    }
}