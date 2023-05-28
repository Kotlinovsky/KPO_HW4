package io.kotlinovsky.restaurant.rent.dtos

import io.kotlinovsky.restaurant.core.serializers.BigDecimalSerializer
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

/**
 * DTO, описывающий блюдо.
 * @property id ID блюда.
 * @property name Название блюда.
 * @property quantity Количество доступных единиц блюда.
 * @property description Описание блюда.
 * @property price Цена блюда за единицу.
 */
@Serializable
data class DishDto(
    @Schema(readOnly = true)
    @SerialName("id")
    val id: Long? = null,
    @SerialName("name")
    @field:Size(min = 1, max = 100, message = "Invalid name format!")
    val name: String,
    @SerialName("quantity")
    @field:PositiveOrZero(message = "Invalid quantity format!")
    val quantity: Int,
    @SerialName("description")
    val description: String,
    @SerialName("price")
    @Serializable(with = BigDecimalSerializer::class)
    @field:PositiveOrZero(message = "Invalid price format!")
    @field:Digits(integer = 10, fraction = 2, message = "Invalid price format!")
    val price: BigDecimal,
)
