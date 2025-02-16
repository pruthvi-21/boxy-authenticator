package com.boxy.authenticator.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import io.ktor.http.encodeURLParameter
import io.ktor.http.encodeURLPath

fun NavController.navigateToHome(popCurrent: Boolean = false) {
    navigate(Routes.Home.base, getNavOptions(popCurrent))
}

fun NavController.navigateToSettings(
    hideSensitiveSettings: Boolean = false,
    popCurrent: Boolean = false,
) {
    val params = "hideSensitiveSettings=$hideSensitiveSettings"
    navigate("${Routes.Settings.base}?$params", getNavOptions(popCurrent))
}

fun NavController.navigateToQrScannerScreen(popCurrent: Boolean = false) {
    navigate(Routes.QrScanner.base, getNavOptions(popCurrent))
}

fun NavController.navigateToNewTokenSetupScreen(popCurrent: Boolean = false) {
    navigate(Routes.TokenSetup.base, getNavOptions(popCurrent))
}

fun NavController.navigateToEditTokenScreen(tokenId: String, popCurrent: Boolean = false) {
    val params = encodeQueryParam("token_id", tokenId)
    navigate("${Routes.TokenSetup.base}?$params", getNavOptions(popCurrent))
}

fun NavController.navigateToNewTokenSetupWithUrl(authUrl: String, popCurrent: Boolean = false) {
    val params = encodeQueryParam("auth_url", authUrl)
    navigate("${Routes.TokenSetup.base}?$params", getNavOptions(popCurrent))
}

fun NavController.navigateToExportTokens() {
    navigate(Routes.ExportTokens.base)
}

fun NavController.navigateToImportTokens() {
    navigate(Routes.ImportTokens.base)
}

private fun NavController.getNavOptions(popCurrent: Boolean): NavOptionsBuilder.() -> Unit = {
    if (popCurrent) {
        currentDestination?.route?.let {
            popUpTo(it) {
                inclusive = true
            }
        }
    }
    launchSingleTop = true
}

private fun encodeQueryParam(key: String, value: String): String {
    val encodedValue = value.encodeURLPath().encodeURLParameter()
    return "$key=$encodedValue"
}
