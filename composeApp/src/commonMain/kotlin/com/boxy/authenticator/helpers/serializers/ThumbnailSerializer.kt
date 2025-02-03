package com.boxy.authenticator.helpers.serializers

import com.boxy.authenticator.data.models.Thumbnail
import com.boxy.authenticator.utils.ThumbnailIcon
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ThumbnailSerializer : KSerializer<Thumbnail> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("thumbnail", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Thumbnail) {
        when (value) {
            is Thumbnail.Color -> encoder.encodeString("color:${value.color}")
            is Thumbnail.Icon -> encoder.encodeString("icon:${value.icon.name}")
        }
    }

    override fun deserialize(decoder: Decoder): Thumbnail {
        val rawString = decoder.decodeString()
        return when {
            rawString.startsWith("color:") -> Thumbnail.Color(rawString.removePrefix("color:"))
            rawString.startsWith("icon:") -> {
                val iconId = rawString.removePrefix("icon:")
                Thumbnail.Icon(ThumbnailIcon.valueOf(iconId))
            }

            else -> throw IllegalArgumentException("Invalid Thumbnail format: $rawString")
        }
    }
}