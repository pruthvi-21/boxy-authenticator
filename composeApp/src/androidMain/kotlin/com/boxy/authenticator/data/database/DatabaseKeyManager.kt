package com.boxy.authenticator.data.database

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey

internal object DatabaseKeyManager {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "boxy_key"
    private const val KEY_ALGORITHM = "HmacSHA512"
    private const val PASSPHRASE_STRING = "sqlcipher_passphrase"

    val databaseKey: ByteArray
        get() {
            return try {
                val mac = Mac.getInstance(KEY_ALGORITHM)
                mac.init(loadKeyFromKeystore())
                mac.doFinal(PASSPHRASE_STRING.toByteArray(Charsets.UTF_8))
            } catch (e: Exception) {
                throw CryptoKeyException("Error generating database key: ${e.localizedMessage}", e)
            }
        }

    private fun loadKeyFromKeystore(): SecretKey {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            keyStore.getKey(KEY_ALIAS, null) as? SecretKey ?: generateKey()
        } catch (e: Exception) {
            throw CryptoKeyException("Error loading key from keystore: ${e.localizedMessage}", e)
        }
    }

    private fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM, ANDROID_KEYSTORE)
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            )
                .setDigests(KeyProperties.DIGEST_SHA512)
                .build()
        )
        return keyGenerator.generateKey()
    }

    class CryptoKeyException(message: String, cause: Throwable) : Exception(message, cause)
}