package org.wysko.autogeokt.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.kotlinx.multik.api.d1array
import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.operations.toList

/**
 * Serializer for [NDArray] class, D1.
 */
object D1ArraySerializer : KSerializer<NDArray<Double, D1>> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = ArraySerializer(Double.serializer()).descriptor

    override fun deserialize(decoder: Decoder): NDArray<Double, D1> {
        val list = mutableListOf<Double>()
        val doubleDescriptor = Double.serializer().descriptor
        decoder.beginStructure(descriptor).let { composite ->
            while (true) {
                if (composite.decodeElementIndex(doubleDescriptor) == CompositeDecoder.DECODE_DONE) break
                list.add(composite.decodeDoubleElement(doubleDescriptor, list.size))
            }
            composite.endStructure(descriptor)
        }
        return mk.d1array(list.size) { list[it] }
    }

    override fun serialize(encoder: Encoder, value: NDArray<Double, D1>) {
        val list = value.toList()
        val doubleDescriptor = Double.serializer().descriptor
        encoder.beginCollection(descriptor, list.size).also { cEncoder ->
            list.forEachIndexed { idx, value -> cEncoder.encodeDoubleElement(doubleDescriptor, idx, value) }
            cEncoder.endStructure(descriptor)
        }
    }
}

/**
 * Serializer for [NDArray] class, D2.
 */
object D2ArraySerializer : KSerializer<NDArray<Double, D2>> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = ArraySerializer(ArraySerializer(Double.serializer())).descriptor

    @OptIn(ExperimentalSerializationApi::class)
    private val doubleArraySerializer = ArraySerializer(Double.serializer())

    private val doubleArrayDescriptor = doubleArraySerializer.descriptor

    @Suppress("NestedBlockDepth")
    override fun deserialize(decoder: Decoder): NDArray<Double, D2> =
        decoder.beginStructure(descriptor).let { composite ->
            mutableListOf<Array<Double>>().apply {
                while (true) {
                    if (composite.decodeElementIndex(doubleArrayDescriptor) == CompositeDecoder.DECODE_DONE) break
                    add(composite.decodeSerializableElement(doubleArrayDescriptor, lastIndex, doubleArraySerializer))
                }
            }.also {
                composite.endStructure(doubleArrayDescriptor)
            }
        }.let { list ->
            // Close the structure, create a flat data list and generate NDArray
            val data = list.flatMap { it.toList() }
            mk.d2array(list.size, list.first().size) { data[it] }
        }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: NDArray<Double, D2>) {
        val matrix = value.toList().chunked(value.shape[1])
        encoder.beginCollection(descriptor, matrix.size).also { outer ->
            matrix.forEachIndexed { index, doubles ->
                outer.encodeSerializableElement(
                    doubleArrayDescriptor,
                    index,
                    ArraySerializer(Double.serializer()),
                    doubles.toTypedArray(),
                )
            }
            outer.endStructure(descriptor)
        }
    }
}

/** Serializer for [D2] class. */
object D2Serializer : KSerializer<D2> {
    override val descriptor = Unit.serializer().descriptor
    override fun deserialize(decoder: Decoder): D2 = D2
    override fun serialize(encoder: Encoder, value: D2) = Unit
}

/** Serializer for [D1] class. */
object D1Serializer : KSerializer<D1> {
    override val descriptor = Unit.serializer().descriptor
    override fun deserialize(decoder: Decoder): D1 = D1
    override fun serialize(encoder: Encoder, value: D1) = Unit
}
