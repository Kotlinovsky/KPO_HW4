package io.kotlinovsky.restaurant.rent.entities

import jakarta.persistence.*
import java.math.BigDecimal

/**
 * Сущность элемента заказа.
 * @property id ID элемента заказа.
 * @property orderId ID заказа.
 * @property dishId ID заказанного блюда.
 * @property quantity Количество заказанного блюда.
 * @property price Цена на момент заказа блюда.
 */
@Entity
@Table(name = "orders_dishes")
data class OrderDishEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    @Column(name = "order_id")
    val orderId: Long? = null,
    @Column(name = "dish_id")
    val dishId: Long? = null,
    @Column(name = "quantity")
    val quantity: Int = 0,
    @Column(name = "price")
    val price: BigDecimal = BigDecimal.ZERO,
)
