package com.boxy.authenticator.core

import com.boxy.authenticator.core.encoding.Base32
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.domain.models.enums.AccountEntryMethod
import com.boxy.authenticator.domain.models.enums.OTPType
import com.boxy.authenticator.domain.models.otp.HotpInfo
import com.boxy.authenticator.domain.models.otp.HotpInfo.Companion.DEFAULT_COUNTER
import com.boxy.authenticator.domain.models.otp.OtpInfo.Companion.DEFAULT_ALGORITHM
import com.boxy.authenticator.domain.models.otp.OtpInfo.Companion.DEFAULT_DIGITS
import com.boxy.authenticator.domain.models.otp.SteamInfo
import com.boxy.authenticator.domain.models.otp.TotpInfo
import com.boxy.authenticator.domain.models.otp.TotpInfo.Companion.DEFAULT_PERIOD
import com.boxy.authenticator.utils.BadlyFormedURLException
import com.boxy.authenticator.utils.EmptyURLContentException
import com.boxy.authenticator.utils.Utils
import com.boxy.authenticator.utils.cleanSecretKey
import io.ktor.http.Url
import io.ktor.http.decodeURLPart
import io.ktor.util.flattenEntries

object TokenEntryParser {
    private const val TAG = "TokenEntryParser"

    fun buildFromUrl(url: String?): TokenEntry {
        if (url.isNullOrEmpty())
            throw EmptyURLContentException("URL data is null or empty")

        val uri = Url(url)

        if (!uri.toString().startsWith("otpauth://")) {
            throw BadlyFormedURLException("Invalid URL format")
        }

        val params = uri.parameters.flattenEntries()
            .associate { it.first to it.second.decodeURLPart() }

        val type = uri.host.let { OTPType.valueOf(it.uppercase()) }

        val issuer = params["issuer"] ?: ""
        var label = uri.encodedPath.substring(1).decodeURLPart()

        if (label.startsWith("$issuer:")) label = label.substringAfter("$issuer:")

        val secret = params["secret"]?.cleanSecretKey()
            ?: throw BadlyFormedURLException("missing secret")
        val secretDecoded = Base32.decode(secret)
        val algorithm = params["algorithm"] ?: DEFAULT_ALGORITHM
        val digits = params["digits"]?.toInt() ?: DEFAULT_DIGITS

        val otpInfo = when (type) {
            OTPType.TOTP -> {
                val period = params["period"]?.toLong() ?: DEFAULT_PERIOD
                TotpInfo(secretDecoded, algorithm, digits, period)
            }

            OTPType.HOTP -> {
                val counter = params["counter"]?.toLong() ?: DEFAULT_COUNTER
                HotpInfo(secretDecoded, algorithm, digits, counter)
            }

            OTPType.STEAM -> {
                SteamInfo(secretDecoded)
            }
        }

        return TokenEntry.create(
            issuer = issuer,
            label = label,
            otpInfo = otpInfo,
            addedFrom = AccountEntryMethod.QR_CODE
        )
    }
}