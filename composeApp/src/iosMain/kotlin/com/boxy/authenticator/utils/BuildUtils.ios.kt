package com.boxy.authenticator.utils

import platform.Foundation.NSBundle

actual object BuildUtils {

    actual fun getAppVersion(): String {
        return NSBundle.mainBundle.infoDictionary?.get("CFBundleVersion") as? String ?: ""
    }

    actual fun getAppVersionName(): String {
        return NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String
            ?: ""
    }
}