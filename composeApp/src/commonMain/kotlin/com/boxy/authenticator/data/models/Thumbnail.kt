package com.boxy.authenticator.data.models

sealed class Thumbnail {
    data class Color(val color: String) : Thumbnail()
    data class Icon(val id: String, val label: String, val path: String) : Thumbnail()
}