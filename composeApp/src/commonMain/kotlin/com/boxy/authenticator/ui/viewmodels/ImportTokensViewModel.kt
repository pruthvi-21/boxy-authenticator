package com.boxy.authenticator.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.usecases.FetchTokenByNameUseCase
import com.boxy.authenticator.domain.usecases.FetchTokensUseCase
import com.boxy.authenticator.domain.usecases.InsertTokensUseCase
import com.boxy.authenticator.utils.name
import kotlinx.coroutines.launch

private const val TAG = "ImportTokensViewModel"

data class ImportItem internal constructor(
    val token: TokenEntry,
    var isChecked: Boolean,
    var isDuplicate: Boolean,
)

class ImportTokensViewModel(
    private val fetchTokensUseCase: FetchTokensUseCase,
    private val insertTokensUseCase: InsertTokensUseCase,
    private val fetchTokenByNameUseCase: FetchTokenByNameUseCase,
) : ViewModel() {

    private val _tokensToImport = mutableStateOf<List<ImportItem>>(emptyList())
    val tokensToImport get() = _tokensToImport.value

    var showRenameTokenDialogWithId = mutableStateOf<String?>(null)
    var showDuplicateWarningDialog = mutableStateOf(false)

    fun importAccounts(tokens: List<ImportItem>, onComplete: () -> Unit) {
        viewModelScope.launch {
            val tokensToInsert = tokens
                .filter { !it.isDuplicate }
                .filter { it.isChecked }
                .map { it.token }

            insertTokensUseCase(tokensToInsert)
            onComplete()
        }
    }

    fun toggleToken(token: ImportItem) {
        _tokensToImport.value = tokensToImport.map { item ->
            if (item == token && !item.isDuplicate) {
                item.copy(isChecked = !item.isChecked)
            } else item
        }
    }

    fun setTokens(tokens: List<TokenEntry>) {
        viewModelScope.launch {
            buildTokensToImportList(tokens)
        }
    }

    private suspend fun buildTokensToImportList(tokens: List<TokenEntry>) {
        fetchTokensUseCase()
            .fold(
                onSuccess = { data ->
                    val existingAccountNames = data.map { it.name }.toSet()
                    val importItems = tokens.map { token ->
                        val isDuplicate = existingAccountNames.contains(token.name)
                        ImportItem(
                            token = token,
                            isChecked = !isDuplicate,
                            isDuplicate = isDuplicate,
                        )
                    }

                    _tokensToImport.value = importItems.sortedBy { it.token.name }
                },
                onFailure = {
                    //TODO: handle errors
                }
            )
    }

    fun updateToken(token: TokenEntry, issuer: String, label: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val updatedList = tokensToImport.map { item ->
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
            _tokensToImport.value = updatedList
            onComplete()
        }
    }

    private suspend fun checkIfDuplicate(token: TokenEntry): Boolean {
        return fetchTokenByNameUseCase(token.issuer, token.label)
            .fold(
                onSuccess = { it != null },
                onFailure = { false }
            )
    }
}