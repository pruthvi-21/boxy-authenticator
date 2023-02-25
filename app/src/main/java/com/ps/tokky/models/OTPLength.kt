package com.ps.tokky.models

import com.ps.tokky.R

enum class OTPLength(
    val id: Int,
    val resId: Int,
    val title: String,
    val value: Int,
    val chunkSize: Int
) {
    LEN_6(0x0045345, R.id.len6, "6 digits", 6, 3),
    LEN_8(0x345A342, R.id.len8, "8 digits", 8, 3);
}