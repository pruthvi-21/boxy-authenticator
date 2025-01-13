package com.boxy.authenticator

internal class AndroidPlatform : Platform {
    override val isAndroid = true
    override val isIos = false
}

actual val platform: Platform
    get() = AndroidPlatform()