package com.boxy.authenticator.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.boxy.authenticator.data.models.otp.OtpInfo
import com.boxy.authenticator.utils.AccountEntryMethod
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "token_entry")
data class TokenEntry(
    @PrimaryKey val id: String,
    var issuer: String,
    var label: String,
    var thumbnail: Thumbnail,
    val otpInfo: OtpInfo,
    val createdOn: Long,
    var updatedOn: Long,
    val addedFrom: AccountEntryMethod,
)
