package io.kotlinovsky.restaurant.rent.models

import java.math.BigDecimal

/**
 * Модель заказанного блюда.
 * @property id ID элемента заказа.
 * @property dishId ID заказанного блюда.
 * @property quantity Количество заказанных блюд.
 * @property price Цена товара на момент заказа.
 */
data class OrderDish(
    val id: Long,
    val dishId: Long,
    val quantity: Int,
    val price: BigDecimal
)
