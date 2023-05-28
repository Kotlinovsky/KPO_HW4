package io.kotlinovsky.restaurant.rent.services

import io.kotlinovsky.restaurant.rent.exceptions.DishesNotEnoughException
import io.kotlinovsky.restaurant.rent.models.Dish
import io.kotlinovsky.restaurant.rent.models.Order
import io.kotlinovsky.restaurant.rent.models.OrderDish
import io.kotlinovsky.restaurant.rent.models.OrderStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Black-box тесты для [OrdersService].
 */
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
internal class OrdersServiceBlackboxTest {

    @Autowired
    lateinit var ordersService: OrdersService
    @Autowired
    lateinit var dishesService: DishesService

    @Test
    fun throwsErrorIfOneOfDishesNotExists() {
        val dish = dishesService.addDish( Dish(id = 1L, name = "First", description = "", quantity = 1, price = BigDecimal.ONE))
        val order = Order(
            id = 1L,
            userId = 1L,
            status = OrderStatus.WAITING,
            specialRequest = "",
            dishes = listOf(
                OrderDish(id = 1L, dishId = dish.id, quantity = 1, price = BigDecimal.ONE),
                OrderDish(id = 2L, dishId = 2L, quantity = 1, price = BigDecimal.ONE)
            ),
        )

        assertThrows<DishesNotEnoughException> { ordersService.createOrder(order) }
    }

    @Test
    fun throwsErrorIfOneOfDishesNotEnough() {
        val firstDish = dishesService.addDish(Dish(id = 1L, name = "First", description = "", quantity = 0, price = BigDecimal.ONE))
        val secondDish = dishesService.addDish(Dish(id = 2L, name = "Second", description = "", quantity = 1, price = BigDecimal.ONE))
        val order = Order(
            id = 1L,
            userId = 1L,
            status = OrderStatus.WAITING,
            specialRequest = "",
            dishes = listOf(
                OrderDish(id = 1L, dishId = firstDish.id, quantity = 1, price = BigDecimal.ONE),
                OrderDish(id = 2L, dishId = secondDish.id, quantity = 1, price = BigDecimal.ONE)
            ),
        )

        assertThrows<DishesNotEnoughException> { ordersService.createOrder(order) }
    }

    @Test
    fun throwsErrorIfTwoAndMoreDishesWithSameIdsNotEnough() {
        val firstDish = dishesService.addDish(Dish(id = 1L, name = "First", description = "", quantity = 1, price = BigDecimal.ONE))
        val secondDish = dishesService.addDish(Dish(id = 2L, name = "Second", description = "", quantity = 1, price = BigDecimal.ONE))
        val order = Order(
            id = 1L,
            userId = 1L,
            status = OrderStatus.WAITING,
            specialRequest = "",
            dishes = listOf(
                OrderDish(id = 1L, dishId = firstDish.id, quantity = 1, price = BigDecimal.ONE),
                OrderDish(id = 2L, dishId = secondDish.id, quantity = 1, price = BigDecimal.ONE),
                OrderDish(id = 3L, dishId = secondDish.id, quantity = 1, price = BigDecimal.ONE)
            ),
        )

        assertThrows<DishesNotEnoughException> { ordersService.createOrder(order) }
    }

    @Test
    fun creationIgnoresIdsFromModel() {
        val firstDish = dishesService.addDish(Dish(id = 1L, name = "First", description = "", quantity = 1, price = BigDecimal.ONE))
        val secondDish = dishesService.addDish(Dish(id = 2L, name = "Second", description = "", quantity = 1, price = BigDecimal.ONE))
        val order = Order(
            id = 10L,
            userId = 1L,
            status = OrderStatus.WAITING,
            specialRequest = "",
            dishes = listOf(
                OrderDish(id = -1L, dishId = firstDish.id, quantity = 1, price = BigDecimal.ONE),
                OrderDish(id = -2L, dishId = secondDish.id, quantity = 1, price = BigDecimal.ONE),
            ),
        )

        val created = ordersService.createOrder(order)
        assertNotEquals(order.id, created.id)
        assertNotEquals(order.dishes[0].id, created.dishes[0].id)
        assertNotEquals(order.dishes[1].id, created.dishes[1].id)
    }

    @Test
    fun creationReservesDishes() {
        val dish = dishesService.addDish(Dish(
            id = 1L,
            quantity = 1,
            name = "First",
            description = "",
            price = BigDecimal.ONE
        ))

        ordersService.createOrder(Order(
            id = 10L,
            userId = 1L,
            specialRequest = "",
            status = OrderStatus.WAITING,
            dishes = listOf(OrderDish(id = 1L, dishId = dish.id, quantity = 1, price = BigDecimal.ONE))
        ))

        assertEquals(0, dishesService.getDish(dish.id)!!.quantity)
    }

    @Test
    fun serviceReturnsOrderIfItExists() {
        val dish = dishesService.addDish(Dish(
            id = 1L,
            quantity = 1,
            name = "First",
            description = "",
            price = BigDecimal(BigInteger.ONE, 2)
        ))

        val created = ordersService.createOrder(Order(
            id = 10L,
            userId = 1L,
            specialRequest = "",
            status = OrderStatus.WAITING,
            dishes = listOf(OrderDish(id = 1L, dishId = dish.id, quantity = 1, price = BigDecimal(BigInteger.ONE, 2)))
        ))

        assertEquals(ordersService.getOrder(created.id), created)
    }

    @Test
    fun serviceReturnsNullIfOrderNotExists() {
        assertNull(ordersService.getOrder(1L))
    }
}
