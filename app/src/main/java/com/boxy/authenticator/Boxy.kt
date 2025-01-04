package com.boxy.authenticator

import android.app.Application
import com.boxy.authenticator.di.initKoin

class Boxy : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(applicationContext)
    }
}