package com.boxy.authenticator.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object FileHelper {
    private const val TAG = "FileHelper"

    fun writeToFile(
        context: Context,
        uri: Uri?,
        content: String,
        password: String,
        onFinished: ((Boolean) -> Unit)? = null
    ) {
        uri ?: return
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                fileWrite(
                    context,
                    uri,
                    encrypt(content, password),
                    onFinished
                )
            }
        }
    }

    private suspend fun fileWrite(
        context: Context,
        uri: Uri,
        content: String,
        onFinished: ((Boolean) -> Unit)?,
    ) {
        var fileOutputStream: OutputStream? = null
        try {
            fileOutputStream = context.contentResolver.openOutputStream(uri)
            withContext(Dispatchers.IO) { fileOutputStream?.write(content.toByteArray()) }
            withContext(Dispatchers.Main) { onFinished?.invoke(true) }
        } catch (exception: Exception) {
            Log.e(TAG, "writeToFile: Unable to write to file", exception)
            withContext(Dispatchers.Main) { onFinished?.invoke(false) }
        } finally {
            withContext(Dispatchers.IO) { fileOutputStream?.close() }
        }
    }

    fun readFromFile(
        context: Context,
        path: Uri,
        password: String,
    ): String? {
        try {
            context.contentResolver.openInputStream(path)?.use { inputStream ->
                inputStream.bufferedReader().use { reader ->
                    val content = reader.readText()
                    return decrypt(content, password)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun encrypt(textToEncrypt: String, password: String): String {
        try {
            val salt = ByteArray(16)
            val iv = ByteArray(16)

            val random = SecureRandom()
            random.nextBytes(salt)
            random.nextBytes(iv)

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, 65536, 256)
            val tmp: SecretKey = factory.generateSecret(spec)
            val secretKey: SecretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)

            val encrypted = cipher.doFinal(textToEncrypt.toByteArray(Charsets.UTF_8))

            val result = ByteArray(encrypted.size + 32)
            System.arraycopy(salt, 0, result, 0, 16)
            System.arraycopy(iv, 0, result, 16, 16)
            System.arraycopy(encrypted, 0, result, 32, encrypted.size)

            return result.joinToString("") { String.format("%02X", it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun decrypt(encryptedText: String, password: String): String {
        try {
            val encryptedData = ByteArray(encryptedText.length / 2)
            for (i in encryptedText.indices step 2) {
                encryptedData[i / 2] = ((Character.digit(encryptedText[i], 16) shl 4)
                        + Character.digit(encryptedText[i + 1], 16)).toByte()
            }

            val salt = ByteArray(16)
            val iv = ByteArray(16)

            System.arraycopy(encryptedData, 0, salt, 0, 16)
            System.arraycopy(encryptedData, 16, iv, 0, 16)

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, 65536, 256)
            val tmp: SecretKey = factory.generateSecret(spec)
            val secretKey: SecretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)

            val decryptedData = cipher.doFinal(encryptedData, 32, encryptedData.size - 32)

            return String(decryptedData, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}