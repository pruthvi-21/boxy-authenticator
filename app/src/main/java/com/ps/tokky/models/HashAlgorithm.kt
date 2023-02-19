package com.ps.tokky.models

import com.ps.tokky.R

enum class HashAlgorithm(
    val id: Int,
) {
    SHA1(R.id.sha1),
    SHA256(R.id.sha256),
    SHA512(R.id.sha512);
}