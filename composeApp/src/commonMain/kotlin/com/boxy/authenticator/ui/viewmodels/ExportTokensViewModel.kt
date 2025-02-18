package com.boxy.authenticator.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.password_empty
import boxy_authenticator.composeapp.generated.resources.password_mismatch
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
import org.jetbrains.compose.resources.getString

class ExportTokensViewModel(
    private val fetchTokensUseCase: FetchTokensUseCase,
) : ViewModel() {

    private val _isEncrypted = mutableStateOf(true)
    val isEncrypted by _isEncrypted

    private val _isUnencryptedAcknowledged = mutableStateOf(false)
    val isUnencryptedAcknowledged by _isUnencryptedAcknowledged

    private val _password = mutableStateOf("")
    val password by _password

    private val _passwordError = mutableStateOf<String?>(null)
    val passwordError by _passwordError

    private val _confirmPassword = mutableStateOf("")
    val confirmPassword by _confirmPassword

    private val _confirmPasswordError = mutableStateOf<String?>(null)
    val confirmPasswordError by _confirmPasswordError

    private val _showPassword = mutableStateOf(false)
    val showPassword by _showPassword

    val isExportEnabled: Boolean
        get() {
            return if (!isEncrypted) isUnencryptedAcknowledged
            else password.isNotEmpty() && confirmPassword.isNotEmpty()
        }

    fun toggleIsUnencryptedAcknowledged() {
        _isUnencryptedAcknowledged.value = !isUnencryptedAcknowledged
    }

    fun toggleShowPassword() {
        _showPassword.value = !showPassword
    }

    fun toggleIsEncrypted() {
        _isEncrypted.value = !isEncrypted
        if (isEncrypted) {
            _password.value = ""
            _passwordError.value = ""
            _confirmPassword.value = ""
            _confirmPasswordError.value = ""
        } else {
            _isUnencryptedAcknowledged.value = false
        }
    }

    fun updatePassword(password: String) {
        _password.value = password
        _passwordError.value = null
    }

    fun updateConfirmPassword(password: String) {
        _confirmPassword.value = password
        _confirmPasswordError.value = null
    }

    private suspend fun verifyFields(): Boolean {
        val password = _password.value
        val confirmPassword = _confirmPassword.value

        if (password.isEmpty()) {
            _passwordError.value = getString(Res.string.password_empty)
            return false
        }
        if (confirmPassword != password) {
            _confirmPasswordError.value = getString(Res.string.password_mismatch)
            return false
        }

        return true
    }

    fun exportTokensToFile(onFinished: (Boolean) -> Unit) = viewModelScope.launch {
        if (isEncrypted && !verifyFields()) return@launch

        fetchTokensUseCase()
            .onSuccess { tokens ->
                val exportData = prepareExportData(tokens)
                val dataToExport = encryptDataIfEnabled(exportData)

                val file = FileKit.saveFile(
                    baseName = buildFileName(),
                    extension = if (!isEncrypted) EXPORT_FILE_EXTENSION else EXPORT_ENCRYPTED_FILE_EXTENSION,
                    bytes = dataToExport,
                )
                onFinished(file != null)
            }
            .onFailure { onFinished(false) }
    }

    private fun prepareExportData(tokens: List<TokenEntry>): String {
        return if (isEncrypted) {
            val exportData = JsonArray(tokens.map { token ->
                BoxyJson.encodeToJsonElement(ExportableTokenEntry.fromTokenEntry(token))
            })
            BoxyJson.encodeToString(exportData)
        } else tokens.joinToString("\n") { it.generateOtpAuthUrl() }
    }

    private suspend fun encryptDataIfEnabled(data: String): ByteArray {
        return if (!isEncrypted) {
            data.encodeToByteArray()
        } else {
            Crypto.encrypt(password, data)
        }
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

    companion object {
        private const val TAG = "ExportTokensViewModel"
    }
}