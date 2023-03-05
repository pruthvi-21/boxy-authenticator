package com.ps.tokky.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object CryptoUtils {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEY_ALIAS = "tokky_key"

    fun getSecretKey(): SecretKey? {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        val key = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        return key ?: createKey(KEY_ALIAS)
    }

    private fun createKey(alias: String): SecretKey? {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        keyGenerator.init(
            KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(false)
                .build()
        )
        return keyGenerator.generateKey()
    }

    fun encryptData(plaintext: String, secretKey: SecretKey?): String {
        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, secretKey)
        }
        val iv = cipher.iv

        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        val result = ByteArray(iv.size + ciphertext.size)

        System.arraycopy(iv, 0, result, 0, iv.size)
        System.arraycopy(ciphertext, 0, result, iv.size, ciphertext.size)

        return Base64.encodeToString(result, Base64.DEFAULT)
    }

    fun decryptData(data: String, secretKey: SecretKey?): String {
        val decoded = Base64.decode(data, Base64.DEFAULT)

        val iv = decoded.copyOfRange(0, 12) // GCM nonce length is 12 bytes
        val ciphertext = decoded.copyOfRange(12, decoded.size)

        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION).apply {
            val spec = GCMParameterSpec(128, iv)
            init(Cipher.DECRYPT_MODE, secretKey, spec)
        }
        val plaintext = cipher.doFinal(ciphertext)
        return String(plaintext, Charsets.UTF_8)
    }
}