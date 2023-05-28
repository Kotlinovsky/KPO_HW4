package io.kotlinovsky.restaurant.rent.services

import io.kotlinovsky.restaurant.rent.entities.OrderDishEntity
import io.kotlinovsky.restaurant.rent.entities.OrderEntity
import io.kotlinovsky.restaurant.rent.exceptions.DishesNotEnoughException
import io.kotlinovsky.restaurant.rent.models.Order
import io.kotlinovsky.restaurant.rent.models.OrderDish
import io.kotlinovsky.restaurant.rent.models.OrderStatus
import io.kotlinovsky.restaurant.rent.repositories.OrderDishesRepository
import io.kotlinovsky.restaurant.rent.repositories.OrdersRepository
import io.kotlinovsky.restaurant.rent.workers.OrdersWorker
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Реализация [OrdersService].
 */
@Service
open class OrdersServiceImpl @Autowired constructor(
    private val ordersWorker: OrdersWorker,
    private val dishesService: DishesService,
    private val ordersRepository: OrdersRepository,
    private val orderDishesRepository: OrderDishesRepository,
) : OrdersService {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @PostConstruct
    fun onCreate() {
        ordersWorker
            .ordersUpdates
            .map { it.toEntity(it.id) }
            .onEach(ordersRepository::saveAndFlush)
            .launchIn(scope)
        ordersWorker.start()
    }

    @PreDestroy
    fun onDestroy() {
        ordersWorker.close()
        scope.cancel()
    }

    @Transactional
    override fun createOrder(order: Order): Order {
        // Проверим, все ли заказываемые блюда есть в базе данных.
        val dishesIds = order.dishes.asSequence().map { it.dishId }.distinct().toList()
        val dishes = dishesService.getDishesByIds(dishesIds).associateBy { it.id }
        if (dishes.size < dishesIds.size) {
            throw DishesNotEnoughException()
        }

        // Так как в заказе могут быть блюда с одинаковыми ID,
        // то нужно просчитать необходимое их количество путем обхода.
        // Затем проверим, хватит ли нам блюд для выполнения заказа.
        val needDishesCount = order.dishes.groupingBy { it.dishId }.eachCount()
        if (needDishesCount.entries.indexOfFirst { (dishId, quantity) -> quantity > dishes[dishId]!!.quantity } != -1) {
            throw DishesNotEnoughException()
        }

        // Сначала сохраним саму сущность заказа.
        // Затем сохраним уже сами элементы заказа.
        // Операция будет атомарной, т.к. сверху мы указали аннотацию @Transactional.
        val orderEntity = ordersRepository.save(order.toEntity())
        val ordersDishes = order.dishes.asSequence().map {
            it.toEntity(orderEntity.id!!)
        }.map {
            orderDishesRepository.save(it)
        }.map {
            it.toModel()
        }.toList()

        // Теперь зарезервируем блюда.
        needDishesCount
            .asSequence()
            .map { dishes[it.key]!!.copy(quantity = dishes[it.key]!!.quantity - it.value) }
            .forEach(dishesService::updateDish)

        return orderEntity
            .toModel(ordersDishes)
            .also(ordersWorker::submit)
    }

    override fun getOrder(id: Long): Order? {
        val orderEntity = ordersRepository.findByIdOrNull(id) ?: return null
        val orderDishesEntities = orderDishesRepository
            .findByOrderId(id)
            .map { it.toModel() }

        return orderEntity.toModel(orderDishesEntities)
    }

    private fun OrderEntity.toModel(dishes: List<OrderDish>): Order {
        return Order(
            id = this.id!!,
            dishes = dishes,
            userId = this.userId!!,
            status = OrderStatus.valueOf(this.status),
            specialRequest = this.specialRequest,
        )
    }

    private fun OrderDishEntity.toModel(): OrderDish {
        return OrderDish(
            id = this.id!!,
            dishId = this.dishId!!,
            quantity = this.quantity,
            price = this.price
        )
    }

    private fun Order.toEntity(id: Long? = null): OrderEntity {
        return OrderEntity(
            id = id,
            userId = this.userId,
            status = this.status.name,
            specialRequest = this.specialRequest,
        )
    }

    private fun OrderDish.toEntity(orderId: Long): OrderDishEntity {
        return OrderDishEntity(
            orderId = orderId,
            price = this.price.setScale(2),
            quantity = this.quantity,
            dishId = this.dishId,
        )
    }
}
