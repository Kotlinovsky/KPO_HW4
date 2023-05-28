package io.kotlinovsky.restaurant.core.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal

/**
 * Сериализатор [BigDecimal].
 * Реализует сериализацию и десериализацию типа
 * для библиотеки kotlinx.serialization.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = BigDecimal::class)
class BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor = PrimitiveSerialDescriptor("java.math.BigDecimal", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: BigDecimal) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): BigDecimal = BigDecimal(decoder.decodeString())
}
