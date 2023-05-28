package io.kotlinovsky.restaurant.rent.dtos

import io.kotlinovsky.restaurant.rent.models.OrderStatus
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO, описывающий заказ.
 * @property id ID заказа.
 * @property status Статус заказа.
 * @property specialRequest Специальный запрос заказчика.
 * @property dishes Элементы заказа.
 */
@Serializable
data class OrderDto(
    @Schema(readOnly = true)
    @SerialName("id")
    val id: Long? = null,
    @SerialName("status")
    @Schema(readOnly = true)
    val status: OrderStatus? = null,
    @SerialName("special_request")
    val specialRequest: String,
    @field:Valid
    @SerialName("order_dishes")
    @field:Size(min = 1, message = "Dishes list cannot be empty!")
    val dishes: List<OrderDishDto>
)
