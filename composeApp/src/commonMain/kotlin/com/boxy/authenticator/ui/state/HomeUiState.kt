package com.boxy.authenticator.ui.state

import com.boxy.authenticator.domain.models.TokenEntry

data class HomeUiState(
    val isLoading: Boolean = false,
    val tokens: List<TokenEntry> = emptyList(),
    val error: String? = null,
    val isInitialLoadComplete: Boolean = false,
    val isFabExpanded: Boolean = false,
    val isLastBackupOutdated: Boolean = false,
    val hasTakenAtleastOneBackup: Boolean = false,
    val isSnackBarVisible: Boolean = true,
)