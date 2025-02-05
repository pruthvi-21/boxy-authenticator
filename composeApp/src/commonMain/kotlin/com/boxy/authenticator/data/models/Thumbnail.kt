package com.boxy.authenticator.data.models

import com.boxy.authenticator.helpers.serializers.BoxyJson
import com.boxy.authenticator.helpers.serializers.ThumbnailSerializer
import com.boxy.authenticator.utils.ThumbnailIcon
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
