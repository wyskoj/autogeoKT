package org.wysko.autogeokt.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Optional

/**
 * A serializer for [Optional] values.
 */
class OptionalSerializer<T>(private val serializer: KSerializer<T>) : KSerializer<Optional<T>> {
    override val descriptor: SerialDescriptor = serializer.descriptor

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Optional<T>) =
        if (value.isPresent) serializer.serialize(encoder, value.get()) else encoder.encodeNull()

    @OptIn(ExperimentalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    override fun deserialize(decoder: Decoder): Optional<T> =
        if (decoder.decodeNotNullMark()) {
            Optional.ofNullable(serializer.deserialize(decoder)) as Optional<T>
        } else {
            Optional.empty<T>() as Optional<T>
        }
}
