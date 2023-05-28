package io.kotlinovsky.restaurant.rent.workers

import io.kotlinovsky.restaurant.rent.models.Order
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс обработчика заказов.
 */
interface OrdersWorker : AutoCloseable {

    /**
     * Поток с обновленными заказами.
     * Является горячим, то есть при подписке не возвращает
     * последние данные, которые были переданы в нем.
     */
    val ordersUpdates: Flow<Order>

    /**
     * Запускает процесс обработки заказов.
     * Реализация должна также обработать заказы,
     * которые были поставлены до запуска обработки.
     */
    fun start()

    /**
     * Ставит заказ в очередь на обработку.
     * Реализация должна принимать заказ даже если очередь переполнена.
     * @param order Заказ, который требуется обработать.
     */
    fun submit(order: Order)
}
