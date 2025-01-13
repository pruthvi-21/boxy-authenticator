package com.boxy.authenticator.utils

import com.google.common.io.BaseEncoding

actual object Base32 {
    @Throws(EncodingException::class)
    actual fun decode(s: String): ByteArray {
        try {
            return BaseEncoding.base32().decode(s.uppercase())
        } catch (e: IllegalArgumentException) {
            throw EncodingException(cause = e)
        }
    }

    actual fun encode(data: ByteArray): String {
        return BaseEncoding.base32().omitPadding().encode(data)
    }
}
