package io.kotlinovsky.restaurant.auth.exceptions

import io.kotlinovsky.restaurant.core.exceptions.ApiException
import org.springframework.http.HttpStatus

/**
 * Исключение неправильных авторизационных данных.
 * Выбрасываемое при неправильных данных при авторизации пользователя.
 */
class InvalidCredentialsException : ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials.")
