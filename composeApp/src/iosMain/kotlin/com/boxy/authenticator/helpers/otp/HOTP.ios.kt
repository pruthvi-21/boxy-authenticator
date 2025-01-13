package com.boxy.authenticator.helpers.otp

import kotlinx.cinterop.*
import platform.CoreCrypto.CCHmac
import platform.CoreCrypto.CC_SHA1_DIGEST_LENGTH
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH
import platform.CoreCrypto.CC_SHA512_DIGEST_LENGTH
import platform.CoreCrypto.kCCHmacAlgSHA1
import platform.CoreCrypto.kCCHmacAlgSHA256
import platform.CoreCrypto.kCCHmacAlgSHA512

@OptIn(ExperimentalForeignApi::class)
internal actual fun getHash(
    secret: ByteArray,
    algo: String,
    counter: Long,
): ByteArray {
    // Convert counter to big-endian bytes
    val counterBytes = ByteArray(8)
    for (i in 0..7) {
        counterBytes[7 - i] = ((counter shr (i * 8)) and 0xFF).toByte()
    }

    val algorithm: UInt = when (algo) {
        "SHA1" -> kCCHmacAlgSHA1
        "SHA256" -> kCCHmacAlgSHA256
        "SHA512" -> kCCHmacAlgSHA512
        else -> throw IllegalArgumentException("Unsupported algorithm: $algo")
    }

    val digestLength: Int = when (algo) {
        "SHA1" -> CC_SHA1_DIGEST_LENGTH
        "SHA256" -> CC_SHA256_DIGEST_LENGTH
        "SHA512" -> CC_SHA512_DIGEST_LENGTH
        else -> throw IllegalArgumentException("Unsupported algorithm: $algo")
    }

    return memScoped {
        val macOut = ByteArray(digestLength)
        val macOutPointer = macOut.refTo(0)

        CCHmac(
            algorithm = algorithm,
            key = secret.refTo(0),
            keyLength = secret.size.toULong(),
            data = counterBytes.refTo(0),
            dataLength = counterBytes.size.toULong(),
            macOut = macOutPointer
        )

        macOut
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.refTo(index: Int): CPointer<ByteVar> {
    return this.usePinned { it.addressOf(index) }
}