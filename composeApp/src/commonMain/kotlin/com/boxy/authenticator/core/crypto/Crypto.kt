package com.boxy.authenticator.core.crypto

import diglol.crypto.XChaCha20
import diglol.crypto.random.nextBytes

object Crypto {

    /**
     * Encrypts the given data using the provided key with XChaCha20.
     * The nonce is prepended to the output.
     *
     * @param password A secret key.
     * @param data The string data to encrypt.
     * @return Encrypted data with the nonce prepended.
     */
    suspend fun encrypt(password: String, data: String): ByteArray {
        val key = KeyGenerator.deriveKey(password.encodeToByteArray())
        require(key.size == 32) { "Key must be 32 bytes long." }

        val nonce = nextBytes(24) // Generate a 24-byte nonce
        val xChaCha20 = XChaCha20(key, nonce, 0)

        val encryptedData = xChaCha20.encrypt(data.encodeToByteArray())

        return nonce + encryptedData
    }

    /**
     * Decrypts the given encrypted data using the provided key with XChaCha20.
     * The nonce is extracted from the input.
     *
     * @param password A secret key.
     * @param data The encrypted data with the nonce prepended.
     * @return The decrypted string.
     */
    suspend fun decrypt(password: String, data: ByteArray): String {
        val key = KeyGenerator.deriveKey(password.encodeToByteArray())
        require(key.size == 32) { "Key must be 32 bytes long." }
        require(data.size > 24) { "Invalid encrypted data: too short to contain a nonce." }

        val nonce = data.sliceArray(0 until 24)
        val encryptedData = data.sliceArray(24 until data.size)

        val xChaCha20 = XChaCha20(key, nonce, 0)

        return xChaCha20.decrypt(encryptedData).decodeToString()
    }
}
