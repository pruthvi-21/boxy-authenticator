package com.boxy.authenticator.helpers

import platform.Foundation.NSLog

actual object Logger {
    actual fun e(tag: String, message: String?, throwable: Throwable?) {
        NSLog("ERROR: [$tag] $message. Throwable: $throwable CAUSE ${throwable?.cause}")
    }

    actual fun e(tag: String, message: String) {
        NSLog("ERROR: [$tag] $message")
    }

    actual fun d(tag: String, message: String) {
        NSLog("DEBUG: [$tag] $message")
    }

    actual fun i(tag: String, message: String) {
        NSLog("INFO: [$tag] $message")
    }
}