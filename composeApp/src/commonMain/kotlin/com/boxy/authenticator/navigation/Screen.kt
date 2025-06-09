package com.boxy.authenticator.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Auth : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data object QrScanner : Screen()

    @Serializable
    data class TokenSetup(
        val tokenId: String? = null,
        val authUrl: String? = null,
    ) : Screen()

    @Serializable
    data class Settings(val hideSensitiveSettings: Boolean = false) : Screen()

    @Serializable
    data object ImportTokens : Screen()

    @Serializable
    data object ExportTokens : Screen()
}
