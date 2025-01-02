package com.ps.tokky.di

import android.content.Context
import com.ps.tokky.data.database.TokensDao
import com.ps.tokky.data.database.TokensDatabase
import com.ps.tokky.data.repositories.TokensRepository
import com.ps.tokky.domain.usecases.DeleteTokenUseCase
import com.ps.tokky.domain.usecases.FetchTokenByIdUseCase
import com.ps.tokky.domain.usecases.FetchTokenByNameUseCase
import com.ps.tokky.domain.usecases.FetchTokensUseCase
import com.ps.tokky.domain.usecases.InsertTokenUseCase
import com.ps.tokky.domain.usecases.InsertTokensUseCase
import com.ps.tokky.domain.usecases.ReplaceExistingTokenUseCase
import com.ps.tokky.helpers.BiometricsHelper
import com.ps.tokky.helpers.TokenFormValidator
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
    fun provideFetchTokensUseCase(tokensRepository: TokensRepository): FetchTokensUseCase {
        return FetchTokensUseCase(tokensRepository)
    }

    @Provides
    fun provideFetchTokenByIdUseCase(tokensRepository: TokensRepository): FetchTokenByIdUseCase {
        return FetchTokenByIdUseCase(tokensRepository)
    }

    @Provides
    fun provideFetchTokenByNameUseCase(tokensRepository: TokensRepository): FetchTokenByNameUseCase {
        return FetchTokenByNameUseCase(tokensRepository)
    }

    @Provides
    fun provideInsertTokenUseCase(tokensRepository: TokensRepository): InsertTokenUseCase {
        return InsertTokenUseCase(tokensRepository)
    }

    @Provides
    fun provideInsertTokensUseCase(tokensRepository: TokensRepository): InsertTokensUseCase {
        return InsertTokensUseCase(tokensRepository)
    }

    @Provides
    fun provideDeleteTokenUseCase(tokensRepository: TokensRepository): DeleteTokenUseCase {
        return DeleteTokenUseCase(tokensRepository)
    }

    @Provides
    fun provideReplaceExistingTokenUseCase(tokensRepository: TokensRepository): ReplaceExistingTokenUseCase {
        return ReplaceExistingTokenUseCase(tokensRepository)
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