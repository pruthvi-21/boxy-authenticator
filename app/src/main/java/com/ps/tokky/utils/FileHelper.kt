package com.ps.tokky.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream

object FileHelper {
    private const val TAG = "FileHelper"

    fun writeToFile(
        context: Context,
        uri: Uri?,
        content: String,
        successCallback: (() -> Unit)? = null,
        failureCallback: (() -> Unit)? = null
    ) {
        uri ?: return
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                fileWrite(context, uri, content, successCallback, failureCallback)
            }
        }
    }

    private suspend fun fileWrite(
        context: Context,
        uri: Uri,
        content: String,
        successCallback: (() -> Unit)? = null,
        failureCallback: (() -> Unit)? = null
    ) {
        var fileOutputStream: OutputStream? = null
        try {
            fileOutputStream = context.contentResolver.openOutputStream(uri)
            withContext(Dispatchers.IO) { fileOutputStream?.write(content.toByteArray()) }
            withContext(Dispatchers.Main) { successCallback?.invoke() }
        } catch (exception: Exception) {
            Log.e(TAG, "writeToFile: Unable to write to file", exception)
            withContext(Dispatchers.Main) { failureCallback?.invoke() }
        } finally {
            withContext(Dispatchers.IO) { fileOutputStream?.close() }
        }
    }
}