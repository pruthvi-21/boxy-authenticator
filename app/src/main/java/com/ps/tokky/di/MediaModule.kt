package com.ps.tokky.di

import android.content.Context
import com.ps.tokky.database.DBHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TokensModule {

    @Provides
    @Singleton
    fun provideDbHelper(@ApplicationContext context: Context): DBHelper {
        return DBHelper.getInstance(context)
    }
}