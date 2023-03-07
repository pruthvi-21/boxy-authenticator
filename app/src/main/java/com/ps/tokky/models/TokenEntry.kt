package com.ps.tokky.models

import android.text.Spannable
import android.util.Log
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base32
import com.ps.tokky.utils.*
import com.ps.tokky.utils.Constants.DEFAULT_HASH_ALGORITHM
import com.ps.tokky.utils.Constants.DEFAULT_OTP_LENGTH
import com.ps.tokky.utils.Constants.DEFAULT_OTP_VALIDITY
import org.json.JSONObject
import java.net.URI
import java.util.*

class TokenEntry {

    val id: String?
    var issuer: String
    var label: String
    private val secretKey: ByteArray
    private val otpLength: Int
    val period: Int
    private val algorithm: String
    var hash: String

    constructor(
        id: String?,
        issuer: String,
        label: String,
        secretKey: String,
        otpLength: Int = DEFAULT_OTP_LENGTH,
        period: Int = DEFAULT_OTP_VALIDITY,
        algorithm: String = DEFAULT_HASH_ALGORITHM,
        hash: String?
    ) {
        this.id = id ?: UUID.randomUUID().toString()
        this.issuer = issuer
        this.label = label

        if (secretKey.isValidSecretKey()) {
            this.secretKey = Base32().decode(secretKey.cleanSecretKey())
        } else throw InvalidSecretKeyException("Invalid secret key")

        this.otpLength = otpLength
        this.period = period
        this.algorithm = algorithm
        this.hash = hash ?: getHash(this)
    }

    constructor(id: String, json: JSONObject) {
        this.id = id
        this.issuer = json.getString(KEY_ISSUER)
        this.label = json.getString(KEY_LABEL)

        val secretKey = json.getString(KEY_SECRET_KEY)
        this.secretKey = Base32().decode(secretKey)

        this.period = if (json.has(KEY_PERIOD)) json.getInt(KEY_PERIOD)
        else DEFAULT_OTP_VALIDITY

        this.otpLength = if (json.has(KEY_OTP_LENGTH)) json.getInt(KEY_OTP_LENGTH)
        else DEFAULT_OTP_LENGTH

        this.algorithm = if (json.has(KEY_ALGORITHM)) {
            json.getString(KEY_ALGORITHM)
        } else DEFAULT_HASH_ALGORITHM

        this.hash = json.getString(KEY_HASH)
    }

    constructor(uri: URI) {
        if (!Utils.isValidTOTPAuthURL(uri.toString())) {
            throw BadlyFormedURLException("Invalid URL format")
        }
        this.id = UUID.randomUUID().toString()

        val params = uri.query.split("&")
            .associate {
                it.split("=")
                    .let { pair -> pair[0] to pair[1] }
            }

        //use uri.host for otp type TOTP or HOTP
        this.issuer = params["issuer"] ?: ""
        this.label = uri.path.substring(1)

        val secret = params["secret"]?.cleanSecretKey() ?: throw IllegalArgumentException("Missing secret parameter")
        if (secret.isValidSecretKey()) {
            this.secretKey = Base32().decode(secret.cleanSecretKey())
        } else throw InvalidSecretKeyException("Invalid secret key")

        this.period = params["period"]?.toInt() ?: 30
        this.otpLength = params["digits"]?.toInt() ?: DEFAULT_OTP_LENGTH

        this.algorithm = params["algorithm"] ?: "SHA1"
        this.hash = getHash(this)
    }

    private var currentOTP: Int = 0
    private var lastUpdatedCounter: Long = 0L

    val secretKeyEncoded: String
        get() {
            return BaseEncoding.base32().encode(secretKey)
        }

    val progressPercent: Long
        get() {
            return (System.currentTimeMillis() / 1000 % period)
        }

    fun updateOTP(): Boolean {
        val time = System.currentTimeMillis() / 1000
        val count = time / period

        if (count > lastUpdatedCounter) {
            currentOTP = TokenCalculator.TOTP_RFC6238(secretKey, period, otpLength, algorithm, 0)
            lastUpdatedCounter = count
            return true
        }
        return false
    }

    val otpFormattedSpan: Spannable
        get() = currentOTP.formatOTP(otpLength)

    val otpFormattedString: String
        get() = "$currentOTP".padStart(otpLength, '0')

    fun updateInfo(issuer: String, label: String) {
        this.issuer = issuer
        this.label = label
        this.hash = getHash(this)
    }

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put(KEY_ISSUER, issuer) //String
            put(KEY_LABEL, label) //String
            put(KEY_SECRET_KEY, secretKeyEncoded) //String
            put(KEY_PERIOD, period) //Int
            put(KEY_OTP_LENGTH, otpLength) //Int
            put(KEY_ALGORITHM, algorithm) //String
            put(KEY_HASH, hash)
        }
    }

    companion object {
        const val TAG = "TokenEntry"

        const val KEY_ISSUER = "issuer"
        const val KEY_LABEL = "label"
        const val KEY_SECRET_KEY = "secret_key"
        const val KEY_ALGORITHM = "algorithm"
        const val KEY_PERIOD = "period"
        const val KEY_OTP_LENGTH = "otp_length"
        const val KEY_HASH = "hash"

        fun getHash(t: TokenEntry): String {
            return "${t.issuer}:${t.period}:${t.label}:${t.otpLength}:${t.secretKeyEncoded}:${t.algorithm}"
                .hash("SHA512")
                .hash("SHA1")
        }

        fun validateHash(tokens: List<TokenEntry>): Boolean {
            var valid = true
            for (token in tokens) {
                if (token.hash != getHash(token)) {
                    Log.e(TAG, "validateHash: hash mismatched for token issued by ${token.issuer}")
                    valid = false
                }
            }
            return valid
        }
    }
}
