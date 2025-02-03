package com.boxy.authenticator.helpers

expect object Logger {
    fun e(tag: String, message: String?, throwable: Throwable? = null)
    fun e(tag: String, message: String)
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
}