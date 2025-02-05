package com.boxy.authenticator.domain.models

import com.boxy.authenticator.domain.models.enums.AccountEntryMethod
import com.boxy.authenticator.domain.models.otp.OtpInfo
import com.boxy.authenticator.utils.Constants
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class TokenEntry(
    val id: String,
    var issuer: String,
    var label: String,
    var thumbnail: Thumbnail,
    val otpInfo: OtpInfo,
    val createdOn: Long,
    var updatedOn: Long,
    val addedFrom: AccountEntryMethod,
) {
    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun create(
            issuer: String,
            label: String,
            thumbnail: Thumbnail = Thumbnail.Color(Constants.THUMBNAIL_COlORS.random()),
            otpInfo: OtpInfo,
            addedFrom: AccountEntryMethod,
        ): TokenEntry {
            return TokenEntry(
                id = Uuid.random().toString(),
                issuer = issuer,
                label = label,
                thumbnail = thumbnail,
                otpInfo = otpInfo,
                createdOn = Clock.System.now().toEpochMilliseconds(),
                updatedOn = Clock.System.now().toEpochMilliseconds(),
                addedFrom = addedFrom
            )
        }
    }
}
