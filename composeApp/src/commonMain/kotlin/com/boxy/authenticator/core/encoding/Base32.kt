package com.boxy.authenticator.core.encoding

expect object Base32 {
    fun decode(s: String): ByteArray
    fun encode(data: ByteArray): String
}