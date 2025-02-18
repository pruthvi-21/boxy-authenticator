package com.boxy.authenticator.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.empty_content
import boxy_authenticator.composeapp.generated.resources.failed_to_decrypt
import boxy_authenticator.composeapp.generated.resources.failed_to_parse_file
import boxy_authenticator.composeapp.generated.resources.no_tokens_to_import
import com.boxy.authenticator.core.Logger
import com.boxy.authenticator.core.TokenEntryParser
import com.boxy.authenticator.core.crypto.Crypto
import com.boxy.authenticator.core.serialization.BoxyJson
import com.boxy.authenticator.domain.models.ExportableTokenEntry
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.usecases.FetchTokenByNameUseCase
import com.boxy.authenticator.domain.usecases.FetchTokensUseCase
import com.boxy.authenticator.domain.usecases.InsertTokensUseCase
import com.boxy.authenticator.utils.name
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PlatformFile
import io.github.vinceglb.filekit.core.pickFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class ImportTokensViewModel(
    private val fetchTokensUseCase: FetchTokensUseCase,
    private val insertTokensUseCase: InsertTokensUseCase,
    private val fetchTokenByNameUseCase: FetchTokenByNameUseCase,
) : ViewModel() {
    private val logger = Logger("ImportTokensViewModel")

    data class ImportItem internal constructor(
        val token: TokenEntry,
        var isChecked: Boolean,
        var isDuplicate: Boolean,
    )

    sealed class UiState {
        data class Initial(val message: String? = null) : UiState()
        data class FileLoaded(val list: List<ImportItem>) : UiState()
        data class RequestPassword(val file: PlatformFile) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val showRenameTokenDialogWithId = mutableStateOf<String?>(null)
    val showDuplicateWarningDialog = mutableStateOf(false)

    fun setInitialState() {
        _uiState.update { UiState.Initial() }
    }

    fun pickFile(isEncrypted: Boolean) = viewModelScope.launch {
        val file = FileKit.pickFile() ?: run {
            logger.e("File selection failed.")
            return@launch
        }

        val fileContent = file.readBytes()
        if (fileContent.isEmpty()) {
            _uiState.update { UiState.Initial(getString(Res.string.empty_content)) }
            return@launch
        }

        if (isEncrypted) {
            _uiState.update { UiState.RequestPassword(file) }
        } else {
            val tokens = decodePlainContent(fileContent.decodeToString())
            when {
                tokens?.isEmpty() == true -> {
                    _uiState.update { UiState.Initial(getString(Res.string.no_tokens_to_import)) }
                }

                tokens != null -> {
                    _uiState.update { UiState.FileLoaded(tokens) }
                }

                else -> {
                    _uiState.update { UiState.Initial(getString(Res.string.failed_to_parse_file)) }
                }
            }
        }
    }

    private fun decodePlainContent(fileContent: String): List<ImportItem>? {
        return try {
            val list = fileContent.split("\n")
            val tokensList = arrayListOf<TokenEntry>()

            list.forEachIndexed { index, line ->
                try {
                    val token = TokenEntryParser.buildFromUrl(line)
                    tokensList.add(token)
                } catch (e: Exception) {
                    logger.e("Error parsing line $index: ${e.message}", e)
                }
            }

            buildImportListFromTokens(tokensList)
        } catch (e: Exception) {
            logger.e(e.message, e)
            null
        }
    }

    fun decodeEncryptedContent(
        file: PlatformFile,
        password: String,
    ) = viewModelScope.launch {
        val fileContent = file.readBytes()

        val decodedData = try {
            val decryptedData = Crypto.decrypt(password, fileContent)
            val list = BoxyJson.decodeFromString<List<ExportableTokenEntry>>(decryptedData)
                .map { it.toTokenEntry() }

            buildImportListFromTokens(list)
        } catch (e: Exception) {
            logger.e(e)
            null
        }

        _uiState.update {
            if (decodedData == null) {
                UiState.Initial(getString(Res.string.failed_to_decrypt))
            } else {
                UiState.FileLoaded(decodedData)
            }
        }
    }

    fun importAccounts(tokens: List<ImportItem>, onComplete: () -> Unit) = viewModelScope.launch {
        val tokensToInsert = tokens
            .filter { !it.isDuplicate }
            .filter { it.isChecked }
            .map { it.token }

        insertTokensUseCase(tokensToInsert)
        onComplete()
    }

    fun toggleToken(token: ImportItem) {
        _uiState.update { currentState ->
            when (currentState) {
                is UiState.FileLoaded -> {
                    val updatedList = currentState.list.map { item ->
                        if (item == token && !item.isDuplicate)
                            item.copy(isChecked = !item.isChecked) else item
                    }
                    UiState.FileLoaded(updatedList)
                }

                else -> currentState
            }
        }
    }

    private fun buildImportListFromTokens(tokens: List<TokenEntry>): List<ImportItem> {
        fetchTokensUseCase()
            .fold(
                onSuccess = { data ->
                    val existingAccountNames = data.map { it.name }.toSet()
                    return tokens.map { token ->
                        val isDuplicate = existingAccountNames.contains(token.name)
                        ImportItem(
                            token = token,
                            isChecked = !isDuplicate,
                            isDuplicate = isDuplicate,
                        )
                    }.sortedBy { it.token.name }
                },
                onFailure = {
                    return tokens.map { token ->
                        ImportItem(
                            token = token,
                            isChecked = true,
                            isDuplicate = false,
                        )
                    }
                }
            )
    }

    fun updateToken(token: TokenEntry, issuer: String, label: String) = viewModelScope.launch {
        if (uiState.value is UiState.FileLoaded) {
            val list = (uiState.value as UiState.FileLoaded).list
            val updatedList = list.map { item ->
                if (item.token.id == token.id) {
                    val updatedToken = item.token.copy(issuer = issuer, label = label)
                    val isStillaDuplicate = checkIfDuplicate(updatedToken)
                    item.copy(
                        token = updatedToken,
                        isDuplicate = isStillaDuplicate,
                        isChecked = !isStillaDuplicate,
                    )
                } else item
            }
            showRenameTokenDialogWithId.value = null
            _uiState.update { UiState.FileLoaded(updatedList) }
        }
    }

    private fun checkIfDuplicate(token: TokenEntry): Boolean {
        return fetchTokenByNameUseCase(token.issuer, token.label)
            .fold(
                onSuccess = { it != null },
                onFailure = { false }
            )
    }
}