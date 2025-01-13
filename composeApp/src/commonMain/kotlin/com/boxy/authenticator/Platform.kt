package com.boxy.authenticator

interface Platform {
    val isAndroid: Boolean
    val isIos: Boolean
}

expect val platform: Platform