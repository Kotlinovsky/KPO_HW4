package io.kotlinovsky.restaurant.rent.repositories

import io.kotlinovsky.restaurant.rent.entities.DishEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Репозиторий для работы с [DishEntity].
 * Осуществляет хранение информации о блюдах.
 */
@Repository
interface DishesRepository : JpaRepository<DishEntity, Long>
