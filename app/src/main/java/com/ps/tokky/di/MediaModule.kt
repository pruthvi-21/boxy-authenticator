package com.ps.tokky.di

import android.content.Context
import com.ps.tokky.database.TokensDao
import com.ps.tokky.database.TokensDatabase
import com.ps.tokky.repositories.TokensRepository
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
}