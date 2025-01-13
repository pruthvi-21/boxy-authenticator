package com.boxy.authenticator.helpers.otp

object HOTP {

    fun generateOTP(secret: ByteArray, algo: String, digits: Int, counter: Long): OTP {
        val hash = getHash(secret, algo, counter)

        // Truncate hash to get the HOTP value
        val offset = hash[hash.size - 1].toInt() and 0xf
        val otp = (((hash[offset].toInt() and 0x7f) shl 24)
                or ((hash[offset + 1].toInt() and 0xff) shl 16)
                or ((hash[offset + 2].toInt() and 0xff) shl 8)
                or (hash[offset + 3].toInt() and 0xff))

        return OTP(otp, digits)
    }
}

internal expect fun getHash(secret: ByteArray, algo: String, counter: Long): ByteArray