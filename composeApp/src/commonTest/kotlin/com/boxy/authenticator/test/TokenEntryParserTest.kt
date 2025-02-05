package com.boxy.authenticator.test

import com.boxy.authenticator.core.TokenEntryParser
import com.boxy.authenticator.core.encoding.Base32
import com.boxy.authenticator.domain.models.TokenEntry
import com.boxy.authenticator.utils.BadlyFormedURLException
import com.boxy.authenticator.utils.EmptyURLContentException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class TokenEntryParserTest {

    @Test
    fun `should parse valid TOTP URL correctly`() {
        val url = "otpauth://totp/jilebi%40gmail.com?secret=JBSWY3DPEHPK3PXP&issuer=Jilebi%20Labs"
        val tokenEntry: TokenEntry = TokenEntryParser.buildFromUrl(url)

        assertNotNull(tokenEntry)
        assertEquals("Jilebi Labs", tokenEntry.issuer)
        assertEquals("jilebi@gmail.com", tokenEntry.label)
        assertEquals("JBSWY3DPEHPK3PXP", Base32.encode(tokenEntry.otpInfo.secretKey))
    }

    @Test
    fun `should throw EmptyURLContentException when URL is null or empty`() {
        assertFailsWith<EmptyURLContentException> {
            TokenEntryParser.buildFromUrl(null)
        }
        assertFailsWith<EmptyURLContentException> {
            TokenEntryParser.buildFromUrl("")
        }
    }

    @Test
    fun `should throw BadlyFormedURLException when URL format is incorrect`() {
        val invalidUrl = "https://example.com/totp?secret=INVALID"
        assertFailsWith<BadlyFormedURLException> {
            TokenEntryParser.buildFromUrl(invalidUrl)
        }
    }

    @Test
    fun `should throw error when required parameters are missing`() {
        val missingSecretUrl = "otpauth://totp/Google:myemail@gmail.com?issuer=Google"
        assertFailsWith<BadlyFormedURLException> {
            TokenEntryParser.buildFromUrl(missingSecretUrl)
        }
    }
}