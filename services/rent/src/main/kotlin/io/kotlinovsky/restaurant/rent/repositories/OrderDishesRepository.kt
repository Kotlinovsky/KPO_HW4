package io.kotlinovsky.restaurant.rent.repositories

import io.kotlinovsky.restaurant.rent.entities.OrderDishEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Репозиторий для работы с [OrderDishEntity].
 * Осуществляет хранение элементов заказов.
 */
@Repository
interface OrderDishesRepository : JpaRepository<OrderDishEntity, Long> {
    /**
     * Ищет элементы заказа с указанным ID.
     * @param orderId ID заказа.
     * @return Список с сущностями элементов заказа.
     */
    fun findByOrderId(orderId: Long): List<OrderDishEntity>
}
