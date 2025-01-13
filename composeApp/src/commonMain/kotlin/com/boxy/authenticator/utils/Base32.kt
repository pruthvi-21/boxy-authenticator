package com.boxy.authenticator.utils

expect object Base32 {
    fun decode(s: String): ByteArray
    fun encode(data: ByteArray): String
}