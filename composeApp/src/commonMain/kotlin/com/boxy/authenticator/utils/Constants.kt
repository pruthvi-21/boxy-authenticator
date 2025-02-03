package com.boxy.authenticator.utils

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

enum class TokenTapResponse {
    NEVER, SINGLE_TAP, DOUBLE_TAP, LONG_PRESS
}

enum class AccountEntryMethod {
    FORM, QR_CODE, RESTORED,
}

enum class OTPType {
    TOTP, HOTP, STEAM,
}

enum class TokenSetupMode {
    NEW, URL, UPDATE
}

enum class ThumbnailIcon(val label: String, val path: String) {
    AMAZON("Amazon", "amazon.png"),
    ATLASSIAN("Atlassian", "atlassian.png"),
    BITBUCKET("Bitbucket", "bitbucket.png"),
    BITWARDEN("Bitwarden", "bitwarden.png"),
    CLOUDFLARE("Cloudflare", "cloudflare.png"),
    COINDCX("CoinDCX", "coindcx.png"),
    DASHLANE("Dashlane", "dashlane.png"),
    DISCORD("Discord", "discord.png"),
    DOCKER("Docker", "docker.png"),
    EVERNOTE("Evernote", "evernote.png"),
    EXPO_GO("Expo Go", "expo_go.png"),
    FACEBOOK("Facebook", "facebook.png"),
    FIGMA("Figma", "figma.png"),
    FIREFOX("Firefox", "firefox.png"),
    GITHUB("GitHub", "github.png"),
    GODADDY("GoDaddy", "godaddy.png"),
    GOOGLE("Google", "google.png"),
    INSTAGRAM("Instagram", "instagram.png"),
    LINKEDIN("LinkedIn", "linkedin.png"),
    MICROSOFT("Microsoft", "microsoft.png"),
    MONGO_DB("MongoDB", "mongo_db.png"),
    MOZILLA("Mozilla", "mozilla.png"),
    NPM("npm", "npm.png"),
    NVIDIA("NVIDIA", "nvidia.png"),
    OLYMP_TRADE("Olymp Trade", "olymp_trade.png"),
    OPENAI("OpenAI", "openai.png"),
    OUTLOOK("Outlook", "outlook.png"),
    PAYPAL("PayPal", "paypal.png"),
    PROTON("Proton", "proton.png"),
    REDDIT("Reddit", "reddit.png"),
    ROCKSTAR_GAMES("Rockstar Games", "rockstar_games.png"),
    SNAPCHAT("Snapchat", "snapchat.png"),
    TRADING_VIEW("TradingView", "trading_view.png"),
    UPSTOX("Upstox", "upstox.png"),
    UPWORK("Upwork", "upwork.png"),
    X("X", "x.png");
}

object Constants {
    val THUMBNAIL_COlORS = listOf(
        "#A0522D",
        "#376B97",
        "#556B2F",
        "#B18F96",
        "#C8AA4B",
    )

    const val THUMBNAIL_ICON_PATH = "drawable/icons"
    const val EXPORT_FILE_NAME_PREFIX = "boxy_accounts_"
    const val EXPORT_FILE_EXTENSION = "json"
}