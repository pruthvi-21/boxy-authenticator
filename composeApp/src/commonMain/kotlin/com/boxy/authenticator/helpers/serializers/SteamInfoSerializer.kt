package com.boxy.authenticator.helpers.serializers

import com.boxy.authenticator.data.models.otp.SteamInfo
import com.boxy.authenticator.utils.Base32
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
        compositeEncoder.encodeStringElement(descriptor, 0, Base32.encode(value.secretKey))
        compositeEncoder.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): SteamInfo {
        val dec = decoder.beginStructure(descriptor)
        var secretKey: ByteArray? = null

        loop@ while (true) {
            when (val index = dec.decodeElementIndex(descriptor)) {
                CompositeDecoder.DECODE_DONE -> break
                0 -> secretKey = Base32.decode(dec.decodeStringElement(descriptor, index))
                else -> throw SerializationException("Unknown index $index")
            }
        }
        dec.endStructure(descriptor)
        return SteamInfo(secretKey!!)
    }
}