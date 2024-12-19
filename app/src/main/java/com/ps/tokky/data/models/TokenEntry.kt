package com.ps.tokky.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base32
import com.ps.tokky.utils.AccountEntryMethod
import com.ps.tokky.utils.Constants.DEFAULT_DIGITS
import com.ps.tokky.utils.Constants.DEFAULT_HASH_ALGORITHM
import com.ps.tokky.utils.Constants.DEFAULT_OTP_TYPE
import com.ps.tokky.utils.Constants.DEFAULT_PERIOD
import com.ps.tokky.utils.HashAlgorithm
import com.ps.tokky.utils.OTPType
import com.ps.tokky.utils.TokenCalculator
import org.json.JSONObject
import java.lang.System.currentTimeMillis
import java.util.Date
import java.util.concurrent.TimeUnit

@Entity(tableName = "token_entry")
data class TokenEntry(
    @PrimaryKey val id: String,
    var issuer: String,
    var label: String,
    val secretKey: String,
    var thumbnailColor: Int,
    var thumbnailIcon: String? = null,
    val type: OTPType,
    val algorithm: HashAlgorithm,
    val digits: Int,
    val period: Int,
    val createdOn: Date,
    var updatedOn: Date,
    val addedFrom: AccountEntryMethod
) {
    @Ignore
    private var secretKeyDecoded: ByteArray? = null

    init {
        secretKeyDecoded = Base32().decode(secretKey)
    }

    @Ignore
    private var currentOTP: Int = 0

    @Ignore
    private var lastUpdatedCounter: Long = 0L

    fun newOtpAvailable(): Boolean {
        val time = TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis())
        val count = time / period

        if (count > lastUpdatedCounter) {
            currentOTP =
                TokenCalculator.TOTP_RFC6238(secretKeyDecoded, period, digits, algorithm.name, 0)
            lastUpdatedCounter = count
            return true
        }
        return false
    }

    val timeRemaining: Long
        get() {
            val currentTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis())
            val timeWithinCurrentOtpPeriod = currentTimeSeconds % period
            return period - timeWithinCurrentOtpPeriod
        }

    val otp: Int
        get() {
            newOtpAvailable()
            return currentOTP
        }

    fun updateInfo(
        issuer: String,
        label: String,
        thumbnailColor: Int = 0,
        thumbnailIcon: String = ""
    ) {
        this.issuer = issuer
        this.label = label
        this.thumbnailColor = thumbnailColor
        this.thumbnailIcon = thumbnailIcon
        updatedOn = Date()
    }

    fun toExportJson(): JSONObject {
        return JSONObject().apply {
            put(KEY_ISSUER, issuer) //String
            put(KEY_LABEL, label) //String
            put(KEY_SECRET_KEY, secretKey) //String
            if (type != DEFAULT_OTP_TYPE) put(KEY_TYPE, type) //Int
            if (period != DEFAULT_PERIOD) put(KEY_PERIOD, period) //Int
            if (digits != DEFAULT_DIGITS) put(KEY_DIGITS, digits) //Int
            if (algorithm != DEFAULT_HASH_ALGORITHM) put(KEY_ALGORITHM, algorithm) //String
            put(KEY_THUMBNAIL_ICON, thumbnailIcon)
            put(KEY_THUMBNAIL_COLOR, thumbnailColor)
        }
    }

    val name: String
        get() {
            if (label.isEmpty()) return issuer
            return "$issuer ($label)"
        }

    companion object {
        const val TAG = "TokenEntry"

        const val KEY_ISSUER = "issuer"
        const val KEY_LABEL = "label"
        const val KEY_SECRET_KEY = "secret_key"
        const val KEY_THUMBNAIL_COLOR = "thumbnail_color"
        const val KEY_THUMBNAIL_ICON = "thumbnail_icon"
        const val KEY_TYPE = "type"
        const val KEY_ALGORITHM = "algorithm"
        const val KEY_PERIOD = "period"
        const val KEY_DIGITS = "digits"
    }
}
