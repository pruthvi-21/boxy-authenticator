package com.boxy.authenticator.domain.models

import com.boxy.authenticator.core.serialization.BoxyJson
import com.boxy.authenticator.core.serialization.ThumbnailSerializer
import com.boxy.authenticator.domain.models.enums.ThumbnailIcon
import kotlinx.serialization.Serializable

@Serializable(with = ThumbnailSerializer::class)
sealed class Thumbnail {
    data class Color(val color: String) : Thumbnail()

    data class Icon(val icon: ThumbnailIcon) : Thumbnail()

    fun serialize(): String = BoxyJson.encodeToString(this)

    companion object {
        fun deserialize(str: String): Thumbnail = BoxyJson.decodeFromString(str)
    }
}
