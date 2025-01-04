package com.boxy.authenticator.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(context: Context, config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        androidContext(context)
        modules(sharedModule, tokensModule)
    }
}
