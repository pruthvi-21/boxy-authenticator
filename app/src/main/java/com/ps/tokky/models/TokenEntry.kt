package com.ps.tokky.models

import android.net.Uri
import android.text.Spannable
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base32
import com.ps.tokky.utils.*
import com.ps.tokky.utils.Constants.DEFAULT_DIGITS
import com.ps.tokky.utils.Constants.DEFAULT_HASH_ALGORITHM
import com.ps.tokky.utils.Constants.DEFAULT_OTP_TYPE
import com.ps.tokky.utils.Constants.DEFAULT_PERIOD
import org.json.JSONObject
import java.util.*

class TokenEntry {

    val id: String
    var issuer: String
    var label: String
    val type: String
    val algorithm: String
    val digits: Int
    val period: Int
    val createdOn: String
    var updatedOn: String private set
    val addedFrom: String

    private val secretKey: String
    private var secretKeyDecoded: ByteArray? = null

    private constructor(
        id: String,
        issuer: String,
        label: String,
        secretKey: String,
        type: String,
        algorithm: String,
        digits: Int,
        period: Int,
        createdOn: String,
        updatedOn: String,
        addedFrom: String
    ) {
        this.id = id
        this.issuer = issuer
        this.label = label
        this.type = type
        this.secretKey = secretKey

        secretKeyDecoded = Base32().decode(secretKey)

        this.algorithm = algorithm
        this.digits = digits
        this.period = period
        this.createdOn = createdOn
        this.updatedOn = updatedOn
        this.addedFrom = addedFrom
    }

    private var currentOTP: Int = 0
    private var lastUpdatedCounter: Long = 0L

    val progressPercent: Long
        get() {
            return (System.currentTimeMillis() / 1000 % period)
        }

    fun updateOTP(): Boolean {
        val time = System.currentTimeMillis() / 1000
        val count = time / period

        if (count > lastUpdatedCounter) {
            currentOTP =
                TokenCalculator.TOTP_RFC6238(secretKeyDecoded, period, digits, algorithm, 0)
            lastUpdatedCounter = count
            return true
        }
        return false
    }

    val otpFormattedSpan: Spannable
        get() = currentOTP.formatOTP(digits)

    val otpFormattedString: String
        get() = "$currentOTP".padStart(digits, '0')

    fun updateInfo(issuer: String, label: String) {
        this.issuer = issuer
        this.label = label
        updatedOn = Date().toString()
    }

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put(KEY_ID, id)
            put(KEY_ISSUER, issuer) //String
            put(KEY_LABEL, label) //String
            put(KEY_SECRET_KEY, secretKey) //String
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
            if (type != DEFAULT_OTP_TYPE.value) put(KEY_TYPE, type) //Int
            if (period != DEFAULT_PERIOD) put(KEY_PERIOD, period) //Int
            if (digits != DEFAULT_DIGITS) put(KEY_DIGITS, digits) //Int
            if (algorithm != DEFAULT_HASH_ALGORITHM) put(KEY_ALGORITHM, algorithm) //String
        }
    }

    val name: String
        get() {
            if (label.isEmpty()) return issuer
            return "$issuer ($label)"
        }

    class BuildFromUrl(private val url: String?) {
        fun build(): TokenEntry {
            val builder = Builder()
            if (url == null)
                throw EmptyURLContentException("URL data is null")

            val uri = Uri.parse(url)

            if (!Utils.isValidTOTPAuthURL(uri.toString())) {
                throw BadlyFormedURLException("Invalid URL format")
            }

            val params = uri.query?.split("&")
                ?.associate {
                    it.split("=")
                        .let { pair -> pair[0] to pair[1] }
                }

            //use uri.host for otp type TOTP or HOTP
            val issuer = params?.get("issuer") ?: ""
            val label = uri.path?.substring(1) ?: ""
            val secret = params?.get("secret")?.cleanSecretKey() ?: ""
            val algorithm = params?.get("algorithm") ?: DEFAULT_HASH_ALGORITHM
            val period = params?.get("period")?.toInt() ?: DEFAULT_PERIOD
            val digits = params?.get("digits")?.toInt() ?: DEFAULT_DIGITS

            return builder
                .setIssuer(issuer)
                .setLabel(label)
                .setSecretKey(secret)
                .setAlgorithm(algorithm)
                .setPeriod(period)
                .setDigits(digits)
                .setAddedFrom(AccountEntryMethod.QR_CODE)
                .build()
        }
    }

    class BuildFromDBJson(private val id: String, private val json: JSONObject) {
        fun build(): TokenEntry {
            val issuer = json.getString(KEY_ISSUER)
            val label = json.getString(KEY_LABEL)
            val secretKey = json.getString(KEY_SECRET_KEY)

            val type = if (json.has(KEY_TYPE)) json.getString(KEY_TYPE)
            else DEFAULT_OTP_TYPE.value

            val period = if (json.has(KEY_PERIOD)) json.getInt(KEY_PERIOD)
            else DEFAULT_PERIOD

            val digits = if (json.has(KEY_DIGITS)) json.getInt(KEY_DIGITS)
            else DEFAULT_DIGITS

            val algorithm = if (json.has(KEY_ALGORITHM)) {
                json.getString(KEY_ALGORITHM)
            } else DEFAULT_HASH_ALGORITHM

            val createdOn = json.getString(KEY_CREATED_ON)
            val updatedOn = json.getString(KEY_UPDATED_ON)
            val addedFrom = json.getString(KEY_ADDED_FROM)

            return TokenEntry(
                id,
                issuer,
                label,
                secretKey,
                type,
                algorithm,
                digits,
                period,
                createdOn,
                updatedOn, addedFrom
            )
        }
    }

    class BuildFromExportJson(private val json: JSONObject) {
        fun build(): TokenEntry {
            val issuer = json.getString(KEY_ISSUER)
            val label = json.getString(KEY_LABEL)
            val secretKey = json.getString(KEY_SECRET_KEY)

            val type = if (json.has(KEY_TYPE)) json.getString(KEY_TYPE)
            else DEFAULT_OTP_TYPE.value

            val period = if (json.has(KEY_PERIOD)) json.getInt(KEY_PERIOD)
            else DEFAULT_PERIOD

            val digits = if (json.has(KEY_DIGITS)) json.getInt(KEY_DIGITS)
            else DEFAULT_DIGITS

            val algorithm = if (json.has(KEY_ALGORITHM)) {
                json.getString(KEY_ALGORITHM)
            } else DEFAULT_HASH_ALGORITHM

            return Builder()
                .setIssuer(issuer)
                .setLabel(label).setSecretKey(secretKey)
                .setPeriod(period)
                .setDigits(digits)
                .setAlgorithm(algorithm)
                .setAddedFrom(AccountEntryMethod.RESTORED)
                .build()
        }
    }

    class Builder {
        private var issuer: String = ""
        private var label: String = ""
        private var secretKey: String = ""
        private var type: String = DEFAULT_OTP_TYPE.value
        private var algorithm = DEFAULT_HASH_ALGORITHM
        private var digits = DEFAULT_DIGITS
        private var period = DEFAULT_PERIOD

        private var addedVia = AccountEntryMethod.FORM

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
                type,
                algorithm,
                digits,
                period,
                Date().toString(),
                Date().toString(),
                addedVia.value
            )
        }
    }

    companion object {
        const val TAG = "TokenEntry"

        const val KEY_ID = "id"
        const val KEY_ISSUER = "issuer"
        const val KEY_LABEL = "label"
        const val KEY_SECRET_KEY = "secret_key"
        const val KEY_TYPE = "type"
        const val KEY_ALGORITHM = "algorithm"
        const val KEY_PERIOD = "period"
        const val KEY_DIGITS = "digits"
        const val KEY_CREATED_ON = "created_on"
        const val KEY_UPDATED_ON = "updated_on"
        const val KEY_ADDED_FROM = "added_from"
    }
}
