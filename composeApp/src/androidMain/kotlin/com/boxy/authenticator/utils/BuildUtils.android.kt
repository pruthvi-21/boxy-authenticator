package com.boxy.authenticator.utils

import com.boxy.authenticator.BuildConfig

actual object BuildUtils {
    actual fun getAppVersion(): String {
        return "${BuildConfig.VERSION_CODE}"
    }

    actual fun getAppVersionName(): String {
        return BuildConfig.VERSION_NAME
    }
}