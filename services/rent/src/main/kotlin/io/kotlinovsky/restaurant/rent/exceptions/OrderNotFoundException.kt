package io.kotlinovsky.restaurant.rent.exceptions

import io.kotlinovsky.restaurant.core.exceptions.ApiException
import org.springframework.http.HttpStatus

/**
 *
 */
class OrderNotFoundException : ApiException(HttpStatus.NOT_FOUND, "Order not found!")
