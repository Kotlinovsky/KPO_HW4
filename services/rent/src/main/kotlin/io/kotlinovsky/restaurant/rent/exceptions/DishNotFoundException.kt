package io.kotlinovsky.restaurant.rent.exceptions

import io.kotlinovsky.restaurant.core.exceptions.ApiException
import org.springframework.http.HttpStatus

/**
 * Исключение отсутствия блюда.
 * Выбрасывается, когда не удается найти в системе
 * блюдо по определенному параметру/параметрам.
 */
class DishNotFoundException : ApiException(HttpStatus.NOT_FOUND, "Dish not found!")
