package com.boxy.authenticator.core

import platform.Foundation.NSLog

actual class Logger actual constructor(private val tag: String) {
    actual fun e(message: String?, throwable: Throwable?) {
        NSLog("ERROR: [$tag] $message. Throwable: $throwable CAUSE ${throwable?.cause}")
    }

    actual fun e(throwable: Throwable?) {
        NSLog("ERROR: [$tag]. Throwable: $throwable CAUSE ${throwable?.cause}")
    }

    actual fun e(message: String) {
        NSLog("ERROR: [$tag] $message")
    }

    actual fun d(message: String) {
        NSLog("DEBUG: [$tag] $message")
    }

    actual fun i(message: String) {
        NSLog("INFO: [$tag] $message")
    }
}