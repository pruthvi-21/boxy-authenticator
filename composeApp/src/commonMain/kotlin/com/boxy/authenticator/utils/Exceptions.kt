package com.boxy.authenticator.utils

import com.boxy.authenticator.data.models.TokenEntry

class InvalidSecretKeyException(msg: String? = "") : Exception(msg)

class TokenNameExistsException(val token: TokenEntry? = null, msg: String? = null) : Exception(msg)
class EmptyURLContentException(msg: String? = "") : Exception(msg)
class BadlyFormedURLException(msg: String? = "") : Exception(msg)

class EncodingException(msg: String? = null, cause: Throwable? = null) : Exception(msg, cause)

class OtpInfoException : Exception {
    constructor(cause: Throwable?) : super(cause)

    constructor(message: String?) : super(message)
}