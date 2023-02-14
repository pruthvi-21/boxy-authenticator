package com.ps.tokky

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val builder = DynamicColorsOptions.Builder()
            .setThemeOverlay(R.style.Theme_Tokky_Overlay)
            .build()

        DynamicColors.applyToActivitiesIfAvailable(this, builder)
    }
}