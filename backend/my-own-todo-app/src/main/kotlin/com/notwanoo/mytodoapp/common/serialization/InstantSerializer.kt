package com.notwanoo.mytodoapp.common.serialization

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object NullableInstantSerializer : KSerializer<Instant?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant? {
        val stringToParse = decoder.decodeString()
        return if (stringToParse.isNotBlank()) {
            Instant.parse(stringToParse)
        } else {
            null
        }
    }

    override fun serialize(encoder: Encoder, value: Instant?) {
        encoder.encodeString(value.toString())
    }
}

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant {
        val stringToParse = decoder.decodeString()
        return if (stringToParse.isNotBlank()) {
            Instant.parse(stringToParse)
        } else {
            // FIXME
            Clock.System.now()
        }
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }
}