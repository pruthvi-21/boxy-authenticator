package com.boxy.authenticator.helpers.serializers

import com.boxy.authenticator.utils.Base32
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ByteArraySerializer : KSerializer<ByteArray> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ByteArray", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ByteArray) {
        try {
            val base32String = Base32.encode(value)
            encoder.encodeString(base32String)
        } catch (e: Exception) {
            throw SerializationException("Failed to serialize ByteArray", e)
        }
    }

    override fun deserialize(decoder: Decoder): ByteArray {
        val base32String = decoder.decodeString()
        return try {
            Base32.decode(base32String)
        } catch (e: Exception) {
            throw SerializationException("Failed to deserialize ByteArray", e)
        }
    }
}