package io.kotlinovsky.restaurant.rent.workers

import io.kotlinovsky.restaurant.rent.models.Order
import io.kotlinovsky.restaurant.rent.models.OrderStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Реализация [OrdersWorker].
 * Реализует очередь заказов с помощью корутин и [Flow].
 */
@Component
class OrdersWorkerImpl @Autowired constructor(
    @Value("\${orders.worker.delay}")
    private val ordersDelayInMillis: Long
) : OrdersWorker {

    private val ordersChannel = Channel<Order>(capacity = BUFFERED)
    private val ordersUpdatesMutable = Channel<Order>(capacity = BUFFERED)
    private val workerScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override val ordersUpdates: Flow<Order>
        get() = ordersUpdatesMutable.consumeAsFlow()

    override fun start() {
        ordersChannel
            .consumeAsFlow()
            .filter { it.status == OrderStatus.WAITING }
            .onEach(::handleOrder)
            .launchIn(workerScope)
    }

    private suspend fun handleOrder(order: Order) {
        ordersUpdatesMutable.trySend(order.copy(status = OrderStatus.IN_WORK))
        delay(ordersDelayInMillis)
        ordersUpdatesMutable.trySend(order.copy(status = OrderStatus.COMPLETED))
    }

    override fun submit(order: Order) {
        ordersChannel.trySend(order)
    }

    override fun close() {
        ordersUpdatesMutable.close()
        ordersChannel.close()
        workerScope.cancel()
    }
}
