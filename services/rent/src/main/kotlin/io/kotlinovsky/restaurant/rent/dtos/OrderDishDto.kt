package io.kotlinovsky.restaurant.rent.dtos

import io.kotlinovsky.restaurant.core.serializers.BigDecimalSerializer
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Positive
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

/**
 * DTO элемента заказа.
 * @property id ID элемента заказа.
 * @property dishId ID заказанного блюда.
 * @property quantity Количество единиц заказанного блюда.
 * @property price Цена единицы заказанного блюда на момент осуществления сделки.
 */
@Serializable
data class OrderDishDto(
    @SerialName("id")
    @Schema(readOnly = true)
    val id: Long? = null,
    @SerialName("dish_id")
    val dishId: Long,
    @SerialName("quantity")
    @field:Positive(message = "Invalid quantity format!")
    val quantity: Int,
    @SerialName("price")
    @Schema(readOnly = true)
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal? = null
)
