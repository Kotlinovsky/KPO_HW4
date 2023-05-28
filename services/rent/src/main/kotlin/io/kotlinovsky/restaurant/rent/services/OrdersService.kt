package io.kotlinovsky.restaurant.rent.services

import io.kotlinovsky.restaurant.rent.exceptions.DishesNotEnoughException
import io.kotlinovsky.restaurant.rent.models.Order
import kotlin.jvm.Throws

/**
 * Интерфейс сервиса заказов.
 * Описывает действия, которые можно осуществлять с заказами.
 */
interface OrdersService {
    /**
     * Создает заказ пользователя.
     * @param order Модель с параметрами заказа.
     * @throws DishesNotEnoughException Если блюда нет в наличии.
     */
    @Throws(DishesNotEnoughException::class)
    fun createOrder(order: Order): Order

    /**
     * Ищет заказ в системе по его ID.
     * @param id ID искомого заказа.
     * @return Модель заказа. Если null, то заказа с таким ID нет.
     */
    fun getOrder(id: Long): Order?
}
