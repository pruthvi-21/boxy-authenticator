package com.ps.tokky.utils

class InvalidSecretKeyException(msg: String? = "") : Exception(msg)

class TokenExistsInDBException(msg: String? = "") : Exception(msg)