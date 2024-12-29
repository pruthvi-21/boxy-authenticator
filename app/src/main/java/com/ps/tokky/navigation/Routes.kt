package com.ps.tokky.navigation

import android.net.Uri

sealed class Routes(val base: String) {
    data object Auth : Routes("/auth")
    data object Home : Routes("/home")
    data object TokenSetup : Routes("/token_setup")
    data object Settings : Routes("/settings")
    data object ExportTokens : Routes("/export")
    data object ImportTokens : Routes("/import")
}

object RouteBuilder {
    fun tokenSetup(tokenId: String? = null, authUrl: String? = null): String {
        return buildRoute(
            Routes.TokenSetup.base,
            mapOf(
                "token_id" to tokenId,
                "auth_url" to authUrl?.let { Uri.encode(it) }
            )
        )
    }

    fun settings(): String {
        return buildRoute(Routes.Settings.base, emptyMap())
    }

    fun export(): String {
        return buildRoute(Routes.ExportTokens.base, emptyMap())
    }

    fun import(fileUri: Uri): String {
        return buildRoute(
            Routes.ImportTokens.base, mapOf(
                "file_uri" to Uri.encode(fileUri.toString())
            )
        )
    }

    private fun buildRoute(base: String, queryParams: Map<String, String?>): String {
        val filteredParams = queryParams.filterValues { !it.isNullOrEmpty() }

        if (filteredParams.isEmpty()) {
            return base
        }
        return "$base?" + filteredParams.entries.joinToString("&") { "${it.key}=${it.value}" }
    }
}