package com.boxy.authenticator.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.usecases.FetchTokensUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val fetchTokensUseCase: FetchTokensUseCase,
) : ViewModel() {

    sealed class UIState<out T> {
        data object Loading : UIState<Nothing>()
        data class Success<out T>(val data: T, val isLoading: Boolean = false) : UIState<T>()
        data class Error(val msg: String? = null) : UIState<Nothing>()
    }

    var _isFabExpanded = mutableStateOf(false)
    val isFabExpanded get() = _isFabExpanded.value

    private val _tokensState = MutableStateFlow<UIState<List<TokenEntry>>>(UIState.Loading)
    val tokensState = _tokensState.asStateFlow()

    fun loadTokens() {
        _tokensState.update { UIState.Loading }
        viewModelScope.launch {
            val result = fetchTokensUseCase()
            result.fold(
                onSuccess = { tokens ->
                    _tokensState.update { UIState.Success(tokens) }
                },
                onFailure = { exception ->
                    _tokensState.update { UIState.Error(exception.message ?: "Unknown error") }
                }
            )
        }
    }

    fun toggleFabState(expand: Boolean = !_isFabExpanded.value) {
        _isFabExpanded.value = expand
    }
}