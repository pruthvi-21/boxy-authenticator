package com.boxy.authenticator.test.core

import com.boxy.authenticator.core.crypto.Crypto
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.experimental.xor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class EncryptionTests {

    private val password = "a_secure_password"

    @Test
    fun testEncryptionNotEmpty() = runBlocking {
        val data = "Some data to encrypt"

        val encryptedData = Crypto.encrypt(password, data)
        assertTrue(encryptedData.isNotEmpty(), "Encrypted data should not be empty.")
    }

    @Test
    fun testEncryptionAndDecryption() = runTest {
        val originalData = "Some data to encrypt"

        val encryptedData = Crypto.encrypt(password, originalData)
        val decryptedData = Crypto.decrypt(password, encryptedData)

        assertEquals(originalData, decryptedData, "Decrypted data should match the original.")
    }

    @Test
    fun testEmptyEncryptionAndDecryption() = runTest {
        val inputData = ""

        val encryptedData = Crypto.encrypt(password, inputData)
        val decryptedData = Crypto.decrypt(password, encryptedData)

        assertEquals(
            inputData,
            decryptedData,
            "Decryption of empty string should return empty string."
        )
    }

    @Test
    fun testEncryptionOfEmptyInput() = runTest {
        val data = ""

        val encryptedData = Crypto.encrypt(password, data)
        assertTrue(
            encryptedData.isNotEmpty(),
            "Encrypted data for an empty string should not be empty."
        )
    }

    @Test
    fun testEncryptionShouldProducesDifferentOutputsForSameInput() = runTest {
        val data = "Some sensitive information"

        val encryptedData1 = Crypto.encrypt(password, data)
        val encryptedData2 = Crypto.encrypt(password, data)

        assertNotEquals(
            encryptedData1,
            encryptedData2,
            "Each encryption should produce a different output due to different nonce."
        )
    }

    @Test
    fun testDecryptionWithWrongKeyProducesGibberish() = runTest {
        val data = "This should not decrypt correctly"

        val encryptedData = Crypto.encrypt(password, data)
        val decryptedData = Crypto.decrypt("a_wrong_password", encryptedData)

        assertNotEquals(
            data,
            decryptedData,
            "Decryption with wrong key should not return original text."
        )
    }

    @Test
    fun testDecryptionOfTamperedCiphertextFails() = runTest {
        val data = "Tamper test"

        val encryptedData = Crypto.encrypt(password, data)
        // Flip a byte
        val tamperedData = encryptedData.copyOf().apply { this[25] = (this[25] xor 0xFF.toByte()) }
        val decryptedData = Crypto.decrypt(password, tamperedData)

        assertNotEquals(data, decryptedData, "Decryption of tampered ciphertext should fail")
    }

    @Test
    fun testEncryptionOfLargeData() = runTest {
        val largeData = "A".repeat(1_000_000)

        val encryptedData = Crypto.encrypt(password, largeData)
        val decryptedData = Crypto.decrypt(password, encryptedData)

        assertEquals(largeData, decryptedData, "Decryption of large data should be accurate")
    }

    @Test
    fun testInvalidCiphertextThrowsError() = runTest {
        val invalidData = ByteArray(10) { it.toByte() }

        assertFailsWith<Exception> {
            Crypto.decrypt(password, invalidData)
        }
    }

    @Test
    fun testNoncePrependingWorks() = runTest {
        val data = "Nonce test"

        val encryptedData = Crypto.encrypt(password, data)

        assertTrue(encryptedData.size > 24, "Encrypted data should be longer than nonce size")
    }
}