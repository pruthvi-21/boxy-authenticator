package com.ps.tokky.helpers.otp

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HOTP {

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun generateOTP(secret: ByteArray, algo: String, digits: Int, counter: Long): OTP {
        val hash = getHash(secret, algo, counter)

        // truncate hash to get the HTOP value
        // http://tools.ietf.org/html/rfc4226#section-5.4
        val offset = hash[hash.size - 1].toInt() and 0xf
        val otp = (((hash[offset].toInt() and 0x7f) shl 24)
                or ((hash[offset + 1].toInt() and 0xff) shl 16)
                or ((hash[offset + 2].toInt() and 0xff) shl 8)
                or (hash[offset + 3].toInt() and 0xff))

        return OTP(otp, digits)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun getHash(secret: ByteArray, algo: String, counter: Long): ByteArray {
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
}
