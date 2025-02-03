package com.boxy.authenticator.helpers

import android.util.Log

actual object Logger {
    actual fun e(tag: String, message: String?, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }

    actual fun e(tag: String, message: String) {
        Log.e(tag, message)
    }

    actual fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    actual fun i(tag: String, message: String) {
        Log.i(tag, message)
    }
}