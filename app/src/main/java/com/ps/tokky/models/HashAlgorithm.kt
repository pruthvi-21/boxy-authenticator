package com.ps.tokky.models

import com.ps.tokky.R

enum class HashAlgorithm(
    val id: Int,
    val resId: Int
) {
    SHA1(0x0045345, R.id.sha1),
    SHA256(0x0034AD3, R.id.sha256),
    SHA512(0xA67D358, R.id.sha512);
}