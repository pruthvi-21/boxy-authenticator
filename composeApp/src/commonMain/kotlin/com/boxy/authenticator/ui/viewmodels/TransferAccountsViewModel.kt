package com.boxy.authenticator.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxy.authenticator.core.Logger
import com.boxy.authenticator.core.crypto.Crypto
import com.boxy.authenticator.core.serialization.BoxyJson
import com.boxy.authenticator.domain.models.ExportableTokenEntry
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.usecases.FetchTokenCountUseCase
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import io.github.vinceglb.filekit.core.pickFile
import kotlinx.coroutines.launch

class TransferAccountsViewModel(
    private val fetchTokenCountUseCase: FetchTokenCountUseCase,
) : ViewModel() {
    private val logger = Logger("TransferAccountsViewModel")

    private val file = mutableStateOf<PlatformFile?>(null)

    private val _requestForPassword = mutableStateOf(false)
    val requestForPassword by _requestForPassword

    fun setRequestForPassword(value: Boolean) {
        _requestForPassword.value = value
    }

    fun importTokensFromFile(
        onSuccess: suspend (List<TokenEntry>) -> Unit,
        onFailure: suspend () -> Unit,
    ) = viewModelScope.launch {
        file.value = FileKit.pickFile(
            type = PickerType.File(
                extensions = listOf("json", "boxy")
            )
        ) ?: return@launch
        val fileContent = file.value!!.readBytes()

        if (fileContent.isEmpty()) {
            onFailure()
            return@launch
        }

        val tokens = decode(fileContent.decodeToString())
        if (tokens != null) {
            onSuccess(tokens)
            return@launch
        }

        _requestForPassword.value = true
    }

    private fun decode(fileContent: String): List<TokenEntry>? {
        return try {
            BoxyJson.decodeFromString<List<ExportableTokenEntry>>(fileContent)
                .map { it.toTokenEntry() }
        } catch (e: Exception) {
            logger.e(e.message, e)
            null
        }
    }

    fun tryDecrypt(
        password: String,
        onSuccess: (List<TokenEntry>) -> Unit,
        onFailed: suspend () -> Unit,
    ) {
        viewModelScope.launch {
            file.value ?: return@launch
            val fileContent = file.value!!.readBytes()
            file.value = null

            val decryptedData = Crypto.decrypt(password, fileContent)
            val decodedData = decode(decryptedData)

            if (decodedData == null) {
                _requestForPassword.value = false
                onFailed()
            } else {
                onSuccess(decodedData)
            }
        }
    }

    fun areTokensAvailable(): Long {
        return fetchTokenCountUseCase.invoke().fold(onSuccess = { it }, onFailure = { 0 })
    }
}