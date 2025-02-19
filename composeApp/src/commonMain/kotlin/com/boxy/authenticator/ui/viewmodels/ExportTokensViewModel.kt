package com.boxy.authenticator.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxy.authenticator.core.AppSettings
import com.boxy.authenticator.core.Logger
import com.boxy.authenticator.core.crypto.Crypto
import com.boxy.authenticator.core.serialization.BoxyJson
import com.boxy.authenticator.domain.models.ExportableTokenEntry
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.models.generateOtpAuthUrl
import com.boxy.authenticator.domain.usecases.FetchTokensUseCase
import com.boxy.authenticator.utils.Constants
import com.boxy.authenticator.utils.Constants.EXPORT_ENCRYPTED_FILE_EXTENSION
import com.boxy.authenticator.utils.Constants.EXPORT_FILE_EXTENSION
import io.github.vinceglb.filekit.core.FileKit
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.encodeToJsonElement

class ExportTokensViewModel(
    private val appSettings: AppSettings,
    private val fetchTokensUseCase: FetchTokensUseCase,
) : ViewModel() {
    private val logger = Logger("ExportTokensViewModel")

    val showPlainTextWarningDialog = mutableStateOf(false)
    val showSetPasswordDialog = mutableStateOf(false)

    private var tokensList = mutableStateListOf<TokenEntry>()
    val tokensFetchError = mutableStateOf(false)
    val areTokensAvailable get() = tokensList.isNotEmpty()

    fun loadAllTokens() {
        tokensFetchError.value = false
        tokensList.clear()

        fetchTokensUseCase().fold(
            onSuccess = { tokensList.addAll(it) },
            onFailure = {
                logger.e(it.message, it)
                tokensFetchError.value = true
            }
        )
    }

    fun exportToPlainTextFile(onDone: (Boolean) -> Unit) = viewModelScope.launch {
        val exportData = tokensList.joinToString("\n") { it.generateOtpAuthUrl() }
        val status = saveToFile(exportData.encodeToByteArray(), EXPORT_FILE_EXTENSION)
        onDone(status)
    }

    fun exportToBoxyFile(password: String, onDone: (Boolean) -> Unit) = viewModelScope.launch {
        val tokensJsonArray = JsonArray(tokensList.map { token ->
            BoxyJson.encodeToJsonElement(ExportableTokenEntry.fromTokenEntry(token))
        })
        val exportData = BoxyJson.encodeToString(tokensJsonArray)
        val encryptedExportData = Crypto.encrypt(password, exportData)
        val status = saveToFile(encryptedExportData, EXPORT_ENCRYPTED_FILE_EXTENSION)
        onDone(status)
    }

    private suspend fun saveToFile(data: ByteArray, extension: String): Boolean {
        val file = FileKit.saveFile(
            baseName = buildFileName(),
            extension = extension,
            bytes = data,
        )

        if (file != null) {
            val currentTimeMillis = Clock.System.now().toEpochMilliseconds()
            appSettings.setLastBackupTimestamp(currentTimeMillis)
        }

        return file != null
    }

    private fun buildFileName(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return Constants.EXPORT_FILE_NAME_PREFIX +
                now.year +
                now.monthNumber.toString().padStart(2, '0') +
                now.dayOfMonth.toString().padStart(2, '0') +
                "_" +
                now.hour.toString().padStart(2, '0') +
                now.minute.toString().padStart(2, '0') +
                now.second.toString().padStart(2, '0')
    }
}