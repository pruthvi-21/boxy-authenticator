package com.ps.tokky.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.data.repositories.TokensRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TokensViewModel @Inject constructor(
    private val tokensRepository: TokensRepository
) : ViewModel() {

    sealed class UIState<out T> {
        data object Loading : UIState<Nothing>()
        data class Success<out T>(val data: T, val isLoading: Boolean = false) : UIState<T>()
    }

    private val _tokensState = MutableStateFlow<UIState<List<TokenEntry>>>(UIState.Loading)
    val tokensState = _tokensState.asStateFlow()

    var tokenToEdit: TokenEntry? = null
    var otpAuthUrl: String? = null

    fun fetchTokens() {
        viewModelScope.launch {
            val tokens = tokensRepository.getAllTokens()
            _tokensState.update { UIState.Success(tokens) }
        }
    }

    fun addToken(
        token: TokenEntry,
        requestCode: String,
        onComplete: (String) -> Unit,
        onDuplicate: (String, TokenEntry) -> Unit
    ) {
        viewModelScope.launch {
            val duplicateToken = findDuplicateToken(token)
            if (duplicateToken != null) {
                onDuplicate(requestCode, duplicateToken)
            } else {
                tokensRepository.insertToken(token)
                onComplete(requestCode)
            }
        }
    }

    fun upsertToken(
        token: TokenEntry,
        requestCode: String,
        onComplete: (String) -> Unit,
        onDuplicate: (String, TokenEntry) -> Unit
    ) {
        viewModelScope.launch {
            val duplicateToken = findDuplicateToken(token)
            if (duplicateToken != null) {
                onDuplicate(requestCode, duplicateToken)
            } else {
                tokensRepository.upsertToken(token)
                onComplete(requestCode)
            }
        }
    }

    fun replaceExistingToken(existingToken: TokenEntry, token: TokenEntry) {
        viewModelScope.launch {
            tokensRepository.deleteToken(existingToken.id)
            tokensRepository.upsertToken(token)
        }
    }

    private suspend fun findDuplicateToken(token: TokenEntry): TokenEntry? {
        return tokensRepository.findDuplicateToken(token.issuer, token.label, token.id)
    }

    fun deleteToken(
        tokenId: String,
        requestCode: String,
        onComplete: (String) -> Unit,
    ) {
        viewModelScope.launch {
            tokensRepository.deleteToken(tokenId)
            onComplete(requestCode)
        }
    }
}
