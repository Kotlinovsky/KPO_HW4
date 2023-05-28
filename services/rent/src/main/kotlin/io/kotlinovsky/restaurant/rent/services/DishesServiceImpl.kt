package io.kotlinovsky.restaurant.rent.services

import io.kotlinovsky.restaurant.rent.entities.DishEntity
import io.kotlinovsky.restaurant.rent.exceptions.DishNotFoundException
import io.kotlinovsky.restaurant.rent.models.Dish
import io.kotlinovsky.restaurant.rent.repositories.DishesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

/**
 * Реализация [DishesService].
 * Осуществляет управления блюдами за счет
 * взаимодействия с [DishesRepository].
 */
@Service
internal class DishesServiceImpl @Autowired constructor(
    private val dishesRepository: DishesRepository
) : DishesService {

    override fun addDish(dish: Dish): Dish {
        return dishesRepository
            .saveAndFlush(dish.toEntity(id = null))
            .toModel()
    }

    override fun updateDish(dish: Dish) {
        val oldEntity = dishesRepository
            .findById(dish.id)
            .orElseThrow { DishNotFoundException() }
        dishesRepository.saveAndFlush(dish.toEntity(entity = oldEntity))
    }

    override fun deleteDish(dishId: Long) {
        val entity = dishesRepository
            .findById(dishId)
            .orElseThrow { DishNotFoundException() }
        dishesRepository.delete(entity)
    }

    override fun getDish(dishId: Long): Dish? {
        return dishesRepository
            .findById(dishId)
            .getOrNull()?.toModel()
    }

    override fun getDishes(): List<Dish> {
        return dishesRepository
            .findAll()
            .map { it.toModel() }
    }

    override fun getDishesByIds(ids: List<Long>): List<Dish> {
        return dishesRepository
            .findAllById(ids)
            .map { it.toModel() }
    }

    private fun Dish.toEntity(id: Long? = this.id, entity: DishEntity = DishEntity()): DishEntity {
        return entity.also {
            it.id = id
            it.name = name
            it.price = price
            it.quantity = quantity
            it.description = description
        }
    }

    private fun DishEntity.toModel() = Dish(
        id = id!!,
        name = name,
        quantity = quantity,
        description = description,
        price = price.setScale(2),
    )
}
