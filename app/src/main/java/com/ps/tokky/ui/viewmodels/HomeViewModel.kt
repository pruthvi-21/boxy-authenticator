package com.ps.tokky.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.domain.usecases.FetchTokensUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fetchTokensUseCase: FetchTokensUseCase,
) : ViewModel() {

    sealed class UIState<out T> {
        data object Loading : UIState<Nothing>()
        data class Success<out T>(val data: T, val isLoading: Boolean = false) : UIState<T>()
        data class Error(val msg: String? = null) : UIState<Nothing>()
    }

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
}
