package io.kotlinovsky.restaurant.rent.services

import io.kotlinovsky.restaurant.rent.exceptions.DishNotFoundException
import io.kotlinovsky.restaurant.rent.models.Dish
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import java.math.BigDecimal

/**
 * Black-box тесты для [DishesService].
 */
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
internal class DishesServiceBlackboxTest {

    @Autowired
    lateinit var dishesService: DishesService

    @Test
    fun dishAddingGeneratesNotSameIds() {
        val dish = Dish(id = 0, name = "Dish", description = "Description", quantity = 1, price = BigDecimal.ONE)
        val firstDish = dishesService.addDish(dish)
        val secondDish = dishesService.addDish(dish)
        assertNotEquals(firstDish.id, secondDish.id)
    }

    @Test
    fun dishAddingNotChangingModelInfo() {
        val dish = Dish(id = 0, name = "Dish", description = "Description", quantity = 1, price = BigDecimal.ONE)
        val firstDish = dishesService.addDish(dish)
        val secondDish = dishesService.addDish(dish)
        assertEquals(firstDish.copy(id = 0L), secondDish.copy(id = 0L))
    }

    @Test
    fun serviceReturnsNullIfDishNotExists() {
        assertNull(dishesService.getDish(0L))
    }

    @Test
    fun serviceReturnsAddedDishes() {
        val dish = Dish(
            id = 0,
            quantity = 1,
            name = "Dish",
            description = "Description",
            price = BigDecimal.valueOf(0L, 2)
        )

        val addedDish = dishesService.addDish(dish)
        assertEquals(addedDish, dishesService.getDish(addedDish.id))
        assertArrayEquals(arrayOf(addedDish), dishesService.getDishes().toTypedArray())
    }

    @Test
    fun serviceUpdatesDishByRequest() {
        val dish = Dish(
            id = 0,
            name = "Dish",
            quantity = 1,
            description = "Description",
            price = BigDecimal.valueOf(0L, 2),
        )

        val addedDish = dishesService.addDish(dish)
        val updatedDish = addedDish.copy(name = "Dish Second")
        dishesService.updateDish(updatedDish)

        assertEquals(updatedDish, dishesService.getDish(addedDish.id))
        assertArrayEquals(arrayOf(updatedDish), dishesService.getDishes().toTypedArray())
    }

    @Test
    fun throwsDishNotFoundExceptionWhenDishNotExistsDuringUpdating() {
        val dish = Dish(id = 0, name = "Dish", description = "Description", quantity = 1, price = BigDecimal.ONE)
        assertThrows<DishNotFoundException> { dishesService.updateDish(dish) }
    }

    @Test
    fun throwsDishNotFoundExceptionWhenDishNotExistsDuringDeleting() {
        assertThrows<DishNotFoundException> { dishesService.deleteDish(0L) }
    }

    @Test
    fun serviceDeletesDishByRequest() {
        val dish = Dish(id = 0, name = "Dish", description = "Description", quantity = 1, price = BigDecimal.ONE)
        val addedDish = dishesService.addDish(dish)
        dishesService.deleteDish(addedDish.id)

        assertNull(dishesService.getDish(addedDish.id))
        assertArrayEquals(arrayOf(), dishesService.getDishes().toTypedArray())
    }

    @Test
    fun serviceReturnsDishesByIds() {
        val first = Dish(
            id = 0,
            name = "First",
            quantity = 1,
            description = "Description",
            price = BigDecimal.valueOf(0L, 2),
        )

        val second = first.copy(name = "Second")
        val addedFirst = dishesService.addDish(first)
        val addedSecond = dishesService.addDish(second)
        val result = dishesService.getDishesByIds(listOf(addedFirst.id, addedSecond.id))
        assertArrayEquals(arrayOf(addedFirst, addedSecond), result.toTypedArray())
    }

    @Test
    fun serviceReturnsDishesByIdsWhenOneOfThisNotExists() {
        val first = Dish(
            id = 0,
            name = "First",
            quantity = 1,
            description = "Description",
            price = BigDecimal.valueOf(0L, 2),
        )

        val second = first.copy(name = "Second")
        val addedFirst = dishesService.addDish(first)
        val addedSecond = dishesService.addDish(second)
        val result = dishesService.getDishesByIds(listOf(addedFirst.id, addedSecond.id, 3L))
        assertArrayEquals(arrayOf(addedFirst, addedSecond), result.toTypedArray())
    }
}
