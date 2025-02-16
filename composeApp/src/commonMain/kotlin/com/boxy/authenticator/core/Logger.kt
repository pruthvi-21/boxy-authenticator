package com.boxy.authenticator.core

expect class Logger(tag: String) {
    fun e(message: String?, throwable: Throwable? = null)
    fun e(throwable: Throwable? = null)
    fun e(message: String)
    fun d(message: String)
    fun i(message: String)
}