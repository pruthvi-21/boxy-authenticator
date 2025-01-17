package com.boxy.authenticator.utils

import com.boxy.authenticator.data.models.Thumbnail

enum class AppTheme {
    LIGHT, DARK, SYSTEM
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

object Constants {
    val DEFAULT_OTP_TYPE = OTPType.TOTP

    val THUMBNAIL_COlORS = listOf(
        "#A0522D",
        "#376B97",
        "#556B2F",
        "#B18F96",
        "#C8AA4B",
    )
    val THUMBNAIL_ICONS = listOf(
        Thumbnail.Icon("amazon", "Amazon", "amazon.png"),
        Thumbnail.Icon("atlassian", "Atlassian", "atlassian.png"),
        Thumbnail.Icon("bitbucket", "Bitbucket", "bitbucket.png"),
        Thumbnail.Icon("bitwarden", "Bitwarden", "bitwarden.png"),
        Thumbnail.Icon("cloudflare", "Cloudflare", "cloudflare.png"),
        Thumbnail.Icon("coindcx", "CoinDCX", "coindcx.png"),
        Thumbnail.Icon("dashlane", "Dashlane", "dashlane.png"),
        Thumbnail.Icon("discord", "Discord", "discord.png"),
        Thumbnail.Icon("docker", "Docker", "docker.png"),
        Thumbnail.Icon("evernote", "Evernote", "evernote.png"),
        Thumbnail.Icon("expo_go", "Expo Go", "expo_go.png"),
        Thumbnail.Icon("facebook", "Facebook", "facebook.png"),
        Thumbnail.Icon("figma", "Figma", "figma.png"),
        Thumbnail.Icon("firefox", "Firefox", "firefox.png"),
        Thumbnail.Icon("github", "GitHub", "github.png"),
        Thumbnail.Icon("godaddy", "GoDaddy", "godaddy.png"),
        Thumbnail.Icon("google", "Google", "google.png"),
        Thumbnail.Icon("instagram", "Instagram", "instagram.png"),
        Thumbnail.Icon("linkedin", "LinkedIn", "linkedin.png"),
        Thumbnail.Icon("microsoft", "Microsoft", "microsoft.png"),
        Thumbnail.Icon("mongo_db", "MongoDB", "mongo_db.png"),
        Thumbnail.Icon("mozilla", "Mozilla", "mozilla.png"),
        Thumbnail.Icon("npm", "npm", "npm.png"),
        Thumbnail.Icon("nvidia", "NVIDIA", "nvidia.png"),
        Thumbnail.Icon("olymp_trade", "Olymp Trade", "olymp_trade.png"),
        Thumbnail.Icon("openai", "OpenAI", "openai.png"),
        Thumbnail.Icon("outlook", "Outlook", "outlook.png"),
        Thumbnail.Icon("paypal", "PayPal", "paypal.png"),
        Thumbnail.Icon("proton", "Proton", "proton.png"),
        Thumbnail.Icon("reddit", "Reddit", "reddit.png"),
        Thumbnail.Icon("rockstar_games", "Rockstar Games", "rockstar_games.png"),
        Thumbnail.Icon("snapchat", "Snapchat", "snapchat.png"),
        Thumbnail.Icon("trading_view", "TradingView", "trading_view.png"),
        Thumbnail.Icon("upstox", "Upstox", "upstox.png"),
        Thumbnail.Icon("upwork", "Upwork", "upwork.png"),
        Thumbnail.Icon("x", "X", "x.png"),
    )

    const val THUMBNAIL_ICON_PATH = "drawable/icons"
    private const val EXPORT_FILE_NAME_PREFIX = "tokky_accounts_"
}