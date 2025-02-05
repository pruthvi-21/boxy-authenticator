package com.boxy.authenticator.core.serialization

import com.boxy.authenticator.domain.models.otp.SteamInfo
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object SteamInfoSerializer : KSerializer<SteamInfo> {
    override val descriptor = buildClassSerialDescriptor("steam") {
        element("secretKey", String.serializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: SteamInfo) {
        val compositeEncoder = encoder.beginStructure(descriptor)
        compositeEncoder.encodeSerializableElement(
            descriptor, 0, ByteArraySerializer, value.secretKey
        )
        compositeEncoder.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): SteamInfo {
        val compositeDecoder = decoder.beginStructure(descriptor)
        var secretKey: ByteArray? = null

        loop@ while (true) {
            when (val index = compositeDecoder.decodeElementIndex(descriptor)) {
                CompositeDecoder.DECODE_DONE -> break
                0 -> secretKey = compositeDecoder.decodeSerializableElement(
                    descriptor, index, ByteArraySerializer
                )
                else -> throw SerializationException("Unknown index $index")
            }
        }
        compositeDecoder.endStructure(descriptor)
        return SteamInfo(secretKey!!)
    }
}