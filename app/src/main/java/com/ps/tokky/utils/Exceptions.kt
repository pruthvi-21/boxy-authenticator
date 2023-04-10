package com.ps.tokky.utils

class InvalidSecretKeyException(msg: String? = "") : Exception(msg)

class TokenExistsInDBException(msg: String? = "") : Exception(msg)
class EmptyURLContentException(msg: String? = "") : Exception(msg)
class BadlyFormedURLException(msg: String? = "") : Exception(msg)