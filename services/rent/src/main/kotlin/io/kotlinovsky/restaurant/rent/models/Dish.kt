package io.kotlinovsky.restaurant.rent.models

import java.math.BigDecimal

/**
 * Модель блюда.
 * @param id ID блюда.
 * @param name Название блюда.
 * @param description Описание блюда.
 * @param quantity Количество блюд на складе.
 * @param price Цена блюда за единицу.
 */
data class Dish(
    val id: Long,
    val name: String,
    val description: String,
    val quantity: Int,
    val price: BigDecimal,
)
