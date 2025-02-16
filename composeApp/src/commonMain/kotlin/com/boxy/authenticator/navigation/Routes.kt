package com.boxy.authenticator.navigation

sealed class Routes(val base: String) {
    data object Auth : Routes("/auth")
    data object Home : Routes("/home")
    data object QrScanner : Routes("/qr-scanner")
    data object TokenSetup : Routes("/token-setup")
    data object Settings : Routes("/settings")
    data object ImportTokens : Routes("/import-tokens")
    data object ExportTokens : Routes("/export-tokens")
}
