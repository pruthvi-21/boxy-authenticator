package com.ps.tokky

import android.app.Application
import com.ps.tokky.di.initKoin

class Tokky : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(applicationContext)
    }
}