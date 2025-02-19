package com.boxy.authenticator.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxy.authenticator.core.AppSettings
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.usecases.FetchTokensUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val appSettings: AppSettings,
    private val fetchTokensUseCase: FetchTokensUseCase,
) : ViewModel() {

    sealed class UIState<out T> {
        data object Loading : UIState<Nothing>()
        data class Success<out T>(val data: T, val isLoading: Boolean = false) : UIState<T>()
        data class Error(val msg: String? = null) : UIState<Nothing>()
    }

    private val _isFabExpanded = mutableStateOf(false)
    val isFabExpanded by _isFabExpanded

    private val _tokensState = MutableStateFlow<UIState<List<TokenEntry>>>(UIState.Loading)
    val tokensState = _tokensState.asStateFlow()

    private val _hasTakenAtleastOneBackup = mutableStateOf(false)
    val hasTakenAtleastOneBackup by _hasTakenAtleastOneBackup

    private val _isLastBackupOutdated = mutableStateOf(false)
    val isLastBackupOutdated by _isLastBackupOutdated

    private val _isSnackBarDismissed = mutableStateOf(false)
    val isSnackBarDismissed by _isSnackBarDismissed

    fun loadTokens() {
        _tokensState.update { UIState.Loading }
        viewModelScope.launch {
            fetchTokensUseCase().fold(
                onSuccess = { tokens ->
                    val disableBackupAlerts = appSettings.isDisableBackupAlertsEnabled()
                    val lastBackupTime = appSettings.getLastBackupTimestamp()

                    if (disableBackupAlerts || tokens.isEmpty()) {
                        _hasTakenAtleastOneBackup.value = true
                        _isLastBackupOutdated.value = false
                    } else {
                        _hasTakenAtleastOneBackup.value = lastBackupTime != -1L
                        _isLastBackupOutdated.value = tokens.any { it.updatedOn > lastBackupTime }
                    }

                    _tokensState.update { UIState.Success(tokens) }
                },
                onFailure = { exception ->
                    _tokensState.update { UIState.Error(exception.message ?: "Unknown error") }
                }
            )
        }
    }

    fun setIsFabExpanded(expanded: Boolean) {
        _isFabExpanded.value = expanded
    }

    fun dismissSnackbar() {
        _isSnackBarDismissed.value = true
    }
}
