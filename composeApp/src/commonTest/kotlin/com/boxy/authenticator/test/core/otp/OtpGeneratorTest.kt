package com.boxy.authenticator.test.core.otp

import com.boxy.authenticator.core.encoding.Base32
import com.boxy.authenticator.core.otp.OtpGenerator
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class OtpGeneratorTest {

    @Test
    fun `should generate valid TOTP for given secret and time`() {
        val secret = Base32.decode("HD6HD7XEBDJSNJV5")
        val time = 1738749344712L

        val algoSHA1 = "SHA1"
        val algoSHA256 = "SHA256"
        val algoSHA512 = "SHA512"
        val period = 30L
        val digits = 10

        val otp1 = OtpGenerator.generateTotp(secret, algoSHA1, period, time)
        val otp256 = OtpGenerator.generateTotp(secret, algoSHA256, period, time)
        val otp512 = OtpGenerator.generateTotp(secret, algoSHA512, period, time)

        assertEquals(637202748, "$otp1".takeLast(digits).toInt())
        assertEquals(486777846, "$otp256".takeLast(digits).toInt())
        assertEquals(903749669, "$otp512".takeLast(digits).toInt())
    }

    @Test
    fun `should change TOTP after each period`() = runTest {
        val secret = Base32.decode("JBSWY3DPEHPK3PXP")
        val algo = "SHA1"
        val period = 10L
        var time = 1738749344712L

        val firstOtp = OtpGenerator.generateTotp(secret, algo, period, time)
        time += (period * 1000L)
        val secondOtp = OtpGenerator.generateTotp(secret, algo, period, time)

        assertNotEquals(firstOtp, secondOtp)
    }

    @Test
    fun `should generate valid HOTP for given secret and counter`() {
        val secret = Base32.decode("HD6HD7XEBDJSNJV5")
        val algoSHA1 = "SHA1"
        val algoSHA256 = "SHA256"
        val algoSHA512 = "SHA512"
        val counter = 100L
        val digits = 10

        val otp1 = OtpGenerator.generateHotp(secret, algoSHA1, counter)
        val otp256 = OtpGenerator.generateHotp(secret, algoSHA256, counter)
        val otp512 = OtpGenerator.generateHotp(secret, algoSHA512, counter)

        assertEquals(868147727, "$otp1".takeLast(digits).toInt())
        assertEquals(1907488328, "$otp256".takeLast(digits).toInt())
        assertEquals(1188780238, "$otp512".takeLast(digits).toInt())
    }
}