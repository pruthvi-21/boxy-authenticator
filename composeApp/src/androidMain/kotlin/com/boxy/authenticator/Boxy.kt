package com.boxy.authenticator

import android.app.Application
import com.boxy.authenticator.di.platformModule
import com.boxy.authenticator.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class Boxy : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@Boxy)
            modules(sharedModule, platformModule)
        }
    }
}