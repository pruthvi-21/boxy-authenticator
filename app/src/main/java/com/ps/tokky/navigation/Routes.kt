package com.ps.tokky.navigation

sealed class Routes(val base: String) {
    data object Home : Routes("/home")
    data object TokenSetup : Routes("/token_setup")
}

object RouteBuilder {
    fun tokenSetup(tokenId: String? = null): String {
        return buildRoute(Routes.TokenSetup.base, mapOf("token_id" to tokenId))
    }

    private fun buildRoute(base: String, queryParams: Map<String, String?>): String {
        val filteredParams = queryParams.filterValues { !it.isNullOrEmpty() }

        if (filteredParams.isEmpty()) {
            return base
        }
        return "$base?" + filteredParams.entries.joinToString("&") { "${it.key}=${it.value}" }
    }
}