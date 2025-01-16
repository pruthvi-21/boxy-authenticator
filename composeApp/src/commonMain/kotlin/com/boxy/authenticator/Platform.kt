package com.boxy.authenticator

expect object Platform {
    val isAndroid: Boolean
    val isIos: Boolean
}