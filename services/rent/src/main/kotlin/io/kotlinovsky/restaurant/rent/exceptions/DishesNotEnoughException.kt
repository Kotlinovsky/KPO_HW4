package io.kotlinovsky.restaurant.rent.exceptions

import io.kotlinovsky.restaurant.core.exceptions.ApiException
import org.springframework.http.HttpStatus

/**
 * Исключение нехватки блюда.
 * Выбрасывается, когда невозможно
 * заказать блюдо из-за его нехватки.
 */
class DishesNotEnoughException : ApiException(HttpStatus.BAD_REQUEST, "Dishes not enough!")
