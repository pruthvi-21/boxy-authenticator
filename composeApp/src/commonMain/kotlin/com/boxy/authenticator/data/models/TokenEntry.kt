package com.boxy.authenticator.data.models

import com.boxy.authenticator.data.models.otp.OtpInfo
import com.boxy.authenticator.utils.AccountEntryMethod
import kotlinx.serialization.Serializable

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
)
