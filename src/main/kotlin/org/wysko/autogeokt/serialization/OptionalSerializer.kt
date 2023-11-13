package org.wysko.autogeokt.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

/**
 * Serializer for [Optional] class.
 */
@ExperimentalSerializationApi
class OptionalSerializer<T : Any>(dataSerializer: KSerializer<T>) : KSerializer<Optional<T>> {
    private val nullableSerializer: KSerializer<T?> = dataSerializer.nullable

    override val descriptor: SerialDescriptor = nullableSerializer.descriptor

    override fun deserialize(decoder: Decoder): Optional<T> {
        val nullableValue: T? = decoder.decodeNullableSerializableValue(nullableSerializer)
        return Optional.ofNullable(nullableValue)
    }

    override fun serialize(encoder: Encoder, value: Optional<T>) {
        encoder.encodeNullableSerializableValue(nullableSerializer, value.orElse(null))
    }
}
