package com.boxy.authenticator.helpers.otp

import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

internal actual fun getHash(
    secret: ByteArray,
    algo: String,
    counter: Long,
): ByteArray {
    val key = SecretKeySpec(secret, "RAW")

    // encode counter in big endian
    val counterBytes = ByteBuffer.allocate(8)
        .order(ByteOrder.BIG_ENDIAN)
        .putLong(counter)
        .array()

    // calculate the hash of the counter
    val mac = Mac.getInstance("Hmac$algo")
    mac.init(key)
    return mac.doFinal(counterBytes)
}