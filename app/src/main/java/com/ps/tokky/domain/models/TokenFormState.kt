package com.ps.tokky.domain.models

import com.ps.tokky.data.models.otp.HotpInfo.Companion.DEFAULT_COUNTER
import com.ps.tokky.data.models.otp.OtpInfo.Companion.DEFAULT_ALGORITHM
import com.ps.tokky.data.models.otp.OtpInfo.Companion.DEFAULT_DIGITS
import com.ps.tokky.data.models.otp.TotpInfo.Companion.DEFAULT_PERIOD
import com.ps.tokky.utils.Constants.THUMBNAIL_COlORS
import com.ps.tokky.utils.OTPType

data class TokenFormState(
    val issuer: String = "",
    val label: String = "",
    val secretKey: String = "",
    val type: OTPType = OTPType.TOTP,
    val thumbnailColor: Int = THUMBNAIL_COlORS.random(),
    val algorithm: String = DEFAULT_ALGORITHM,
    val period: String = "$DEFAULT_PERIOD",
    val digits: String = "$DEFAULT_DIGITS",
    val counter: String = "$DEFAULT_COUNTER",
    val enableAdvancedOptions: Boolean = false,
    val isAlgorithmFieldVisible: Boolean = true,
    val isDigitsFieldVisible: Boolean = true,
    val isPeriodFieldVisible: Boolean = true,
    val isCounterFieldVisible: Boolean = false,
    val validationErrors: Map<String, String?> = emptyMap(),
)