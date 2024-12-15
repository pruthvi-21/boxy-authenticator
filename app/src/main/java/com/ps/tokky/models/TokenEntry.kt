package com.ps.tokky.models

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base32
import com.ps.tokky.utils.AccountEntryMethod
import com.ps.tokky.utils.Constants.DEFAULT_DIGITS
import com.ps.tokky.utils.Constants.DEFAULT_HASH_ALGORITHM
import com.ps.tokky.utils.Constants.DEFAULT_OTP_TYPE
import com.ps.tokky.utils.Constants.DEFAULT_PERIOD
import com.ps.tokky.utils.InvalidSecretKeyException
import com.ps.tokky.utils.OTPType
import com.ps.tokky.utils.TokenCalculator
import com.ps.tokky.utils.cleanSecretKey
import com.ps.tokky.utils.formatOTP
import com.ps.tokky.utils.isValidSecretKey
import org.json.JSONObject
import java.lang.System.currentTimeMillis
import java.util.Date
import java.util.UUID
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
    val algorithm: String,
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
    private var currentFormattedOTP: String = ""

    @Ignore
    private var lastUpdatedCounter: Long = 0L

    fun newOtpAvailable(): Boolean {
        val time = TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis())
        val count = time / period

        if (count > lastUpdatedCounter) {
            currentOTP =
                TokenCalculator.TOTP_RFC6238(secretKeyDecoded, period, digits, algorithm, 0)
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

    val otpFormatted: String
        get() {
            if (currentOTP != otp) {
                currentFormattedOTP = currentOTP.formatOTP(digits)
            }
            return currentFormattedOTP
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

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put(KEY_ID, id)
            put(KEY_ISSUER, issuer) //String
            put(KEY_LABEL, label) //String
            put(KEY_SECRET_KEY, secretKey) //String
            put(KEY_THUMBNAIL_COLOR, thumbnailColor) //Int
            put(KEY_THUMBNAIL_ICON, thumbnailIcon) //String
            put(KEY_PERIOD, period) //Int
            put(KEY_DIGITS, digits) //Int
            put(KEY_ALGORITHM, algorithm) //String
            put(KEY_CREATED_ON, createdOn) //String
            put(KEY_UPDATED_ON, updatedOn) //String
            put(KEY_ADDED_FROM, addedFrom) //String
        }
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

    class Builder {
        private var issuer: String = ""
        private var label: String = ""
        private var secretKey: String = ""
        private var type = DEFAULT_OTP_TYPE
        private var algorithm = DEFAULT_HASH_ALGORITHM
        private var digits = DEFAULT_DIGITS
        private var period = DEFAULT_PERIOD

        private var addedVia = AccountEntryMethod.FORM
        private var thumbnailColor = Color.BLACK
        private var thumbnailIcon = ""

        fun setIssuer(issuer: String): Builder {
            this.issuer = issuer
            return this
        }

        fun setLabel(label: String): Builder {
            this.label = label
            return this
        }

        fun setSecretKey(secretKey: String): Builder {
            this.secretKey = secretKey
            return this
        }

        fun setAlgorithm(algorithm: String): Builder {
            this.algorithm = algorithm
            return this
        }

        fun setDigits(digits: Int): Builder {
            this.digits = digits
            return this
        }

        fun setPeriod(period: Int): Builder {
            this.period = period
            return this
        }

        fun setAddedFrom(via: AccountEntryMethod): Builder {
            this.addedVia = via
            return this
        }

        fun setThumbnailColor(@ColorInt color: Int): Builder {
            this.thumbnailColor = color
            return this
        }

        fun setThumbnailIcon(iconStr: String): Builder {
            this.thumbnailIcon = iconStr
            return this
        }

        fun build(): TokenEntry {
            if (this.issuer == "") throw Exception("Issuer can't be empty")
            if (this.secretKey == "") throw Exception("Secret key can't be empty")
            else if (!secretKey.cleanSecretKey()
                    .isValidSecretKey()
            ) throw InvalidSecretKeyException("Invalid secret key")

            return TokenEntry(
                UUID.randomUUID().toString(),
                issuer,
                label,
                secretKey.cleanSecretKey(),
                thumbnailColor,
                thumbnailIcon,
                type,
                algorithm,
                digits,
                period,
                Date(),
                Date(),
                addedVia
            )
        }
    }

    companion object {
        const val TAG = "TokenEntry"

        const val KEY_ID = "id"
        const val KEY_ISSUER = "issuer"
        const val KEY_LABEL = "label"
        const val KEY_SECRET_KEY = "secret_key"
        const val KEY_THUMBNAIL_COLOR = "thumbnail_color"
        const val KEY_THUMBNAIL_ICON = "thumbnail_icon"
        const val KEY_TYPE = "type"
        const val KEY_ALGORITHM = "algorithm"
        const val KEY_PERIOD = "period"
        const val KEY_DIGITS = "digits"
        const val KEY_CREATED_ON = "created_on"
        const val KEY_UPDATED_ON = "updated_on"
        const val KEY_ADDED_FROM = "added_from"
    }
}
