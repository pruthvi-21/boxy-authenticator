package com.ps.tokky.models

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.text.Spannable
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base32
import com.ps.tokky.utils.Constants.DEFAULT_HASH_ALGORITHM
import com.ps.tokky.utils.Constants.DEFAULT_OTP_LENGTH
import com.ps.tokky.utils.Constants.DEFAULT_OTP_VALIDITY
import com.ps.tokky.utils.TokenCalculator
import com.ps.tokky.utils.cleanSecretKey
import com.ps.tokky.utils.formatOTP
import java.net.URL

class TokenEntry(
    var dbID: Int = -1,
    var issuer: String,
    var label: String,
    private val secretKey: ByteArray,
    val otpLength: OTPLength,
    val period: Int,
    val algorithm: HashAlgorithm
) : Parcelable {

    private var currentOTP: Int = 0
    private var lastUpdatedCounter: Long = 0L

    constructor(
        issuer: String,
        label: String,
        secretKey: String,
        otpLength: OTPLength,
        period: Int,
        algorithm: HashAlgorithm
    ) : this(-1, issuer, label, Base32().decode(secretKey), otpLength, period, algorithm) {
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createByteArray()!!,
        OTPLength.valueOf(parcel.readString()!!),
        parcel.readInt(),
        HashAlgorithm.valueOf(parcel.readString()!!)
    ) {
    }

    val secretKeyEncoded: String
        get() {
            return String(Base32().encode(secretKey))
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
        get() = "$currentOTP".padStart(otpLength.value, '0')

    override fun toString(): String {
        return "Issuer: $issuer\n" +
                "Label: $label\n" +
                "SecretKey: $secretKeyEncoded\n" +
                "OTP: ${currentOTP}\n"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(dbID)
        parcel.writeString(issuer)
        parcel.writeString(label)
        parcel.writeByteArray(secretKey)
        parcel.writeString(otpLength.name)
        parcel.writeInt(period)
        parcel.writeString(algorithm.name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TokenEntry> {
        override fun createFromParcel(parcel: Parcel): TokenEntry {
            return TokenEntry(parcel)
        }

        override fun newArray(size: Int): Array<TokenEntry?> {
            return arrayOfNulls(size)
        }
    }

    class Builder {
        private var dbID: Int = -1
        private lateinit var issuer: String
        private var label: String = ""
        private lateinit var secretKey: ByteArray
        private var otpLength = DEFAULT_OTP_LENGTH
        private var period = DEFAULT_OTP_VALIDITY
        private var algorithm = DEFAULT_HASH_ALGORITHM

        fun setIssuer(issuer: String): Builder {
            this.issuer = issuer
            return this
        }

        fun setLabel(label: String?): Builder {
            if (label != null) {
                this.label = label
            }
            return this
        }

        fun setSecretKey(key: String): Builder {
            return setSecretKey(Base32().decode(key.cleanSecretKey()))
        }

        fun setSecretKey(key: ByteArray): Builder {
            this.secretKey = key
            return this
        }

        fun setOTPLength(otpLength: OTPLength): Builder {
            this.otpLength = otpLength
            return this
        }

        fun setPeriod(period: Int?): Builder {
            if (period != null) {
                this.period = period
            }
            return this
        }

        fun setHashAlgorithm(algorithm: HashAlgorithm): Builder {
            this.algorithm = algorithm
            return this
        }

        fun createFromQR(path: String): Builder {
            val urlPath = path.replaceFirst("otpauth".toRegex(), "http")
            val uri = Uri.parse(urlPath)
            val url = URL(urlPath)
            if (url.protocol != "http") {
                throw Exception("Invalid Protocol")
            }

            val issuer = uri.getQueryParameter("issuer") ?: throw Exception("Empty issuer name")
            setIssuer(issuer)

            val secret = uri.getQueryParameter("secret") ?: throw Exception("Empty secret")
            setSecretKey(secret)

            var label: String? = ""
            if (uri.path != null) label = getStrippedLabel(issuer, uri.path!!.substring(1))
            setLabel(label)

            val period = uri.getQueryParameter("period")
            setPeriod(period?.toInt())

            val length = uri.getQueryParameter("digits")
            if (length != null) {
                if (length == "8")
                    setOTPLength(OTPLength.LEN_8)
            }

            val algorithm = uri.getQueryParameter("algorithm")
            if (algorithm != null) {
                if (algorithm.equals("sha265", true))
                    setHashAlgorithm(HashAlgorithm.SHA256)
                if (algorithm.equals("sha512", true))
                    setHashAlgorithm(HashAlgorithm.SHA512)
            }
            return this
        }

        fun build(): TokenEntry {
            return TokenEntry(
                dbID = dbID,
                issuer = issuer,
                label = label,
                secretKey = secretKey,
                otpLength = otpLength,
                period = period,
                algorithm = algorithm
            )
        }

        private fun getStrippedLabel(issuer: String?, label: String): String? {
            return if (issuer == null || issuer.isEmpty() || !label.startsWith("$issuer:")) {
                label.trim { it <= ' ' }
            } else {
                label.substring(issuer.length + 1).trim { it <= ' ' }
            }
        }
    }
}
