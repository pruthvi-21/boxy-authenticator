package com.boxy.authenticator.core

import android.util.Log

actual class Logger actual constructor(private val tag: String) {
    actual fun e(message: String?, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }

    actual fun e(throwable: Throwable?) {
        Log.e(tag, "${throwable?.message}", throwable)
    }

    actual fun e(message: String) {
        Log.e(tag, message)
    }

    actual fun d(message: String) {
        Log.d(tag, message)
    }

    actual fun i(message: String) {
        Log.i(tag, message)
    }
}