package com.boxy.authenticator.core

expect object Platform {
    val isAndroid: Boolean
    val isIos: Boolean
}