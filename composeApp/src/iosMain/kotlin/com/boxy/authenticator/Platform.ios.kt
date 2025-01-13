package com.boxy.authenticator

internal class IOSPlatform : Platform {
    override val isAndroid = false
    override val isIos = true
}

actual val platform: Platform
    get() = IOSPlatform()