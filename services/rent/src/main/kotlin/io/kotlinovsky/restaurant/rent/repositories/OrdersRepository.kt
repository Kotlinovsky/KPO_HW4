package io.kotlinovsky.restaurant.rent.repositories

import io.kotlinovsky.restaurant.rent.entities.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Репозиторий для работы с [OrderEntity].
 * Осуществляет хранение информации о заказах.
 */
interface OrdersRepository : JpaRepository<OrderEntity, Long>
