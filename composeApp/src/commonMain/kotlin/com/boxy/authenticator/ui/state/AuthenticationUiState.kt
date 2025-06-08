package com.boxy.authenticator.ui.state

data class AuthenticationUiState(
    val password: String = "",
    val passwordError: String? = null,
    val isVerifyingPassword: Boolean = false,
)