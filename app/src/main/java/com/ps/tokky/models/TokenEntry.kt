package com.ps.tokky.models

import android.os.Parcel
import android.os.Parcelable
import android.text.Spannable
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base32
import com.ps.tokky.utils.TokenCalculator
import com.ps.tokky.utils.formatOTP

class TokenEntry(
    val issuer: String,
    val label: String,
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
    ) : this(issuer, label, Base32().decode(secretKey), otpLength, period, algorithm) {
    }

    constructor(parcel: Parcel) : this(
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
}
