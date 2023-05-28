package io.kotlinovsky.restaurant.rent.models

/**
 * Модель заказа.
 * @property id ID заказа.
 * @property userId ID заказчика.
 * @property status Статус выполнения заказа.
 * @property specialRequest Специальный запрос заказчика.
 * @property dishes Заказанные блюда.
 */
data class Order(
    val id: Long,
    val userId: Long,
    val status: OrderStatus,
    val specialRequest: String,
    val dishes: List<OrderDish>
)
